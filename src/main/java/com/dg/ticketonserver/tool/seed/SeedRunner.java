package com.dg.ticketonserver.tool.seed;

import com.dg.ticketonserver.seat.domain.SeatGrade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {

    private static final int USER_COUNT = 1_000;
    private static final int CONCERT_COUNT = 10;
    private static final int VENUE_COUNT = 5;
    private static final int SCHEDULE_PER_CONCERT = 50;
    private static final int UPCOMING_SCHEDULE_PER_CONCERT = 5;
    private static final int ROWS_PER_SCHEDULE = 20;
    private static final int SEATS_PER_ROW = 50;
    private static final int SEATS_PER_SCHEDULE = ROWS_PER_SCHEDULE * SEATS_PER_ROW;

    private static final String SEED_PASSWORD = "loadtest1234!";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (alreadySeeded()) {
            return;
        }

        long startedAt = System.nanoTime();

        measure("users", USER_COUNT + 1, this::insertUsers);
        measure("concerts", CONCERT_COUNT, this::insertConcerts);
        measure("schedules", CONCERT_COUNT * SCHEDULE_PER_CONCERT, this::insertSchedules);
        measure("seats", CONCERT_COUNT * SCHEDULE_PER_CONCERT * SEATS_PER_SCHEDULE, this::insertSeats);

        log.info("시드 완료 — {}", elapsed(startedAt));
    }

    private boolean alreadySeeded() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM concerts", Integer.class);

        if (count != null && count > 0) {
            log.warn("이미 시드 데이터가 있습니다 (concerts={}건). 다시 넣으려면 테이블을 비우세요.", count);
            return true;
        }

        return false;
    }

    private void insertUsers() {
        String encodedPassword = passwordEncoder.encode(SEED_PASSWORD);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.update("""
                INSERT INTO users (username, password, nickname, role, created_at, updated_at)
                VALUES ('admin', ?, '관리자', 'ADMIN', ?, ?)
                """, encodedPassword, now, now);

        jdbcTemplate.batchUpdate("""
                        INSERT INTO users (username, password, nickname, role, created_at, updated_at)
                        VALUES (?, ?, ?, 'USER', ?, ?)
                        """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, "loadtest%04d".formatted(i + 1));
                        ps.setString(2, encodedPassword);
                        ps.setString(3, "테스터%04d".formatted(i + 1));
                        ps.setTimestamp(4, now);
                        ps.setTimestamp(5, now);
                    }

                    @Override
                    public int getBatchSize() {
                        return USER_COUNT;
                    }
                });
    }

    private void insertConcerts() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.batchUpdate("""
                        INSERT INTO concerts (title, content, venue, start_date, end_date, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        LocalDate startDate = concertStartDate(i);

                        ps.setString(1, "테스트 공연 %02d".formatted(i + 1));
                        ps.setString(2, "테스트 공연 %02d 상세 설명입니다.".formatted(i + 1));
                        ps.setString(3, "테스트 공연장 %02d".formatted((i % VENUE_COUNT) + 1));
                        ps.setObject(4, startDate);
                        ps.setObject(5, startDate.plusDays(lastScheduleDayOffset()));
                        ps.setTimestamp(6, now);
                        ps.setTimestamp(7, now);
                    }

                    @Override
                    public int getBatchSize() {
                        return CONCERT_COUNT;
                    }
                });
    }

    private void insertSchedules() {
        List<Long> concertIds = jdbcTemplate.queryForList(
                "SELECT concert_id FROM concerts ORDER BY concert_id", Long.class);

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < concertIds.size(); i++) {
            long concertId = concertIds.get(i);
            LocalDate startDate = concertStartDate(i);

            jdbcTemplate.batchUpdate("""
                            INSERT INTO schedules (concert_id, round_number, start_at, open_at)
                            VALUES (?, ?, ?, ?)
                            """,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int j) throws SQLException {
                            int roundNumber = j + 1;

                            ps.setLong(1, concertId);
                            ps.setInt(2, roundNumber);
                            ps.setTimestamp(3, Timestamp.valueOf(startAtOf(startDate, roundNumber)));
                            ps.setTimestamp(4, Timestamp.valueOf(openAtOf(now, roundNumber)));
                        }

                        @Override
                        public int getBatchSize() {
                            return SCHEDULE_PER_CONCERT;
                        }
                    });
        }
    }

    private void insertSeats() {
        List<Long> scheduleIds = jdbcTemplate.queryForList(
                "SELECT schedule_id FROM schedules ORDER BY schedule_id", Long.class);

        for (long scheduleId : scheduleIds) {
            jdbcTemplate.batchUpdate("""
                            INSERT INTO seats (schedule_id, seat_number, grade, price, status, version)
                            VALUES (?, ?, ?, ?, 'AVAILABLE', 0)
                            """,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            int row = (i / SEATS_PER_ROW) + 1;
                            int number = (i % SEATS_PER_ROW) + 1;
                            SeatGrade grade = gradeOf(row);

                            ps.setLong(1, scheduleId);
                            ps.setString(2, row + "열-" + number);
                            ps.setString(3, grade.name());
                            ps.setInt(4, priceOf(grade));
                        }

                        @Override
                        public int getBatchSize() {
                            return SEATS_PER_SCHEDULE;
                        }
                    });
        }
    }

    private LocalDate concertStartDate(int concertIndex) {
        return LocalDate.now().plusDays(30L + concertIndex);
    }

    private int lastScheduleDayOffset() {
        return (SCHEDULE_PER_CONCERT - 1) / 2;
    }

    private LocalDateTime startAtOf(LocalDate startDate, int roundNumber) {
        int dayOffset = (roundNumber - 1) / 2;
        int hour = (roundNumber % 2 == 1) ? 14 : 19;

        return startDate.plusDays(dayOffset).atTime(hour, 0);
    }

    private LocalDateTime openAtOf(LocalDateTime now, int roundNumber) {
        int upcomingFrom = SCHEDULE_PER_CONCERT - UPCOMING_SCHEDULE_PER_CONCERT;

        if (roundNumber <= upcomingFrom) {
            return now.minusDays(7);
        }

        return now.plusMinutes(10L * (roundNumber - upcomingFrom));
    }

    private SeatGrade gradeOf(int row) {
        if (row <= 2) {
            return SeatGrade.VIP;
        }
        if (row <= 8) {
            return SeatGrade.R;
        }
        if (row <= 16) {
            return SeatGrade.S;
        }
        return SeatGrade.A;
    }

    private int priceOf(SeatGrade grade) {
        return switch (grade) {
            case VIP -> 180_000;
            case R -> 140_000;
            case S -> 110_000;
            case A -> 80_000;
        };
    }

    private void measure(String name, int rows, Runnable task) {
        long startedAt = System.nanoTime();
        task.run();
        log.info("{} {}행 — {}", name, rows, elapsed(startedAt));
    }

    private String elapsed(long startedAt) {
        return "%.2fs".formatted((System.nanoTime() - startedAt) / 1_000_000_000.0);
    }
}
