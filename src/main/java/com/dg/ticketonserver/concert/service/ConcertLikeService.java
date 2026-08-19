package com.dg.ticketonserver.concert.service;

import com.dg.ticketonserver.concert.domain.Concert;
import com.dg.ticketonserver.concert.domain.ConcertLike;
import com.dg.ticketonserver.concert.exception.ConcertErrorCode;
import com.dg.ticketonserver.concert.repository.ConcertLikeRepository;
import com.dg.ticketonserver.concert.repository.ConcertRepository;
import com.dg.ticketonserver.global.exception.BusinessException;
import com.dg.ticketonserver.user.domain.User;
import com.dg.ticketonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertLikeService {

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final ConcertLikeRepository concertLikeRepository;

    public void likeConcert(Long concertId, Long userId) {
        if (!concertRepository.existsById(concertId)) {
            throw new BusinessException(ConcertErrorCode.CONCERT_NOT_FOUND);
        }

        if (concertLikeRepository.existsByConcertIdAndUserId(concertId, userId)) {
            throw new BusinessException(ConcertErrorCode.ALREADY_LIKED);
        }

        User user = userRepository.getReferenceById(userId);
        Concert concert = concertRepository.getReferenceById(concertId);

        concertRepository.increaseLikeCount(concertId);
        ConcertLike concertLike = ConcertLike.of(concert, user);
        concertLikeRepository.save(concertLike);
    }

    public void unlikeConcert(Long concertId, Long userId) {
        if (concertLikeRepository.deleteByConcertIdAndUserId(concertId, userId) == 0) {
            throw new BusinessException(ConcertErrorCode.LIKE_NOT_FOUND);
        }

        concertRepository.decreaseLikeCount(concertId);
    }
}
