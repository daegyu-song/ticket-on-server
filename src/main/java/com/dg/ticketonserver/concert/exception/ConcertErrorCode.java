package com.dg.ticketonserver.concert.exception;

import com.dg.ticketonserver.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConcertErrorCode implements ErrorCode {

    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트를 찾을 수 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요하지 않은 콘서트입니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요한 콘서트입니다.");

    private final HttpStatus status;
    private final String message;
}
