package com.dg.ticketonserver.concert.service;

import com.dg.ticketonserver.TestcontainersConfiguration;
import com.dg.ticketonserver.user.domain.User;
import com.dg.ticketonserver.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.hikari.maximum-pool-size=10",
        "spring.jpa.show-sql=false"
})
@Import(TestcontainersConfiguration.class)
class ConcertLikeConcurrencyTest {

    @Autowired
    private ConcertLikeService concertLikeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long concertId;
    private final List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void 콘서트_1개와_유저_100명을_준비한다() {
        jdbcTemplate.update("DELETE FROM concert_likes");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM concerts");
        userIds.clear();

        jdbcTemplate.update("""
                INSERT INTO concerts (title, venue, start_date, end_date, like_count, created_at, updated_at)
                VALUES ('동시성 테스트', '테스트홀', CURRENT_DATE, CURRENT_DATE, 0, NOW(), NOW())
                """);
        concertId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        for (int i = 0; i < 100; i++) {
            userIds.add(userRepository.save(User.createUser("user" + i, "pw", "nick" + i)).getId());
        }
    }

    @Test
    @Disabled("수정 전 순서 재현용. 실패 화면이 필요할 때만 수동 실행한다")
    @DisplayName("[수정 전] INSERT 를 먼저 하면 데드락이 난다")
    void insert_를_먼저_하면_데드락이_난다() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        CountDownLatch done = new CountDownLatch(100);
        AtomicInteger deadlock = new AtomicInteger();

        for (Long userId : userIds) {
            pool.submit(() -> {
                try {
                    transactionTemplate.executeWithoutResult(status -> {
                        // 자식 INSERT → 부모 concerts 행에 S 락
                        jdbcTemplate.update("INSERT INTO concert_likes (concert_id, user_id, created_at, updated_at)"
                                + " VALUES (?, ?, NOW(), NOW())", concertId, userId);
                        // 같은 행에 X 락 요구 → 남이 쥔 S 때문에 승격 불가
                        jdbcTemplate.update("UPDATE concerts SET like_count = like_count + 1"
                                + " WHERE concert_id = ?", concertId);
                    });
                } catch (CannotAcquireLockException e) {
                    deadlock.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }
        done.await();
        pool.shutdown();

        System.out.printf("%n[수정 전] 성공 %d / 데드락 %d / like_count %d / 실제 행 수 %d%n%n",
                100 - deadlock.get(), deadlock.get(), likeCount(), rowCount());

        assertThat(deadlock.get()).isGreaterThan(0);
        assertThat(likeCount()).isEqualTo(rowCount());   // 실패해도 정합성은 지켜진다
    }

    @Test
    @DisplayName("[현재] 100명이 동시에 좋아요를 눌러도 카운트는 정확히 100")
    void 백명이_동시에_좋아요를_눌러도_카운트는_정확히_100() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        CountDownLatch done = new CountDownLatch(100);

        for (Long userId : userIds) {
            pool.submit(() -> {
                try {
                    concertLikeService.likeConcert(concertId, userId);
                } catch (Exception e) {
                    System.out.println("실패: " + e.getClass().getSimpleName());
                } finally {
                    done.countDown();
                }
            });
        }
        done.await();
        pool.shutdown();

        System.out.printf("%n[현재] like_count %d / 실제 행 수 %d%n%n", likeCount(), rowCount());

        assertThat(likeCount()).isEqualTo(100);
        assertThat(rowCount()).isEqualTo(100);
    }

    private int likeCount() {
        return jdbcTemplate.queryForObject(
                "SELECT like_count FROM concerts WHERE concert_id = ?", Integer.class, concertId);
    }

    private int rowCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM concert_likes WHERE concert_id = ?", Integer.class, concertId);
    }
}
