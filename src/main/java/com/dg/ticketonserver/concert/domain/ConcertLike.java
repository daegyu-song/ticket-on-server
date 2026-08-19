package com.dg.ticketonserver.concert.domain;

import com.dg.ticketonserver.global.domain.BaseTimeEntity;
import com.dg.ticketonserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "concert_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_concert_likes_concert_user",
                columnNames = {"concert_id", "user_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertLike extends BaseTimeEntity {

    @Id
    @Column(name = "concert_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private ConcertLike(Concert concert, User user) {
        this.concert = concert;
        this.user = user;
    }

    public static ConcertLike of(Concert concert, User user) {
        return new ConcertLike(concert, user);
    }
}
