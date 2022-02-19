package com.bob.mate.domain.user.service;

import com.bob.mate.domain.user.dto.AccessTokenResponse;
import com.bob.mate.domain.user.dto.LoginMember;
import com.bob.mate.domain.user.dto.RefreshTokenRequest;
import com.bob.mate.domain.user.entity.User;
import com.bob.mate.global.config.redis.RedisUtil;
import com.bob.mate.global.exception.CustomException;
import com.bob.mate.global.exception.ErrorCode;
import com.bob.mate.global.jwt.JwtTokenProvider;
import com.bob.mate.global.jwt.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RedisUtil redisUtil;

    /**
     * access token 이 유효한지 확인
     */
    public void validateAccessToken(String accessToken) {
        accessTokenExtractor(accessToken);
    }



    /**
     * 토큰으로 회원 조회
     */
    @Transactional(readOnly = true)
    public LoginMember findMemberByToken(String accessToken) {
        if (!accessToken.isEmpty()) {
            accessTokenExtractor(accessToken);
        }

        Long id = Long.parseLong(jwtTokenProvider.getPayload(accessToken));
        User findUser = userService.findById(id);
        return new LoginMember(findUser.getId());
    }

    /**
     * refresh Token 으로 Access Token 이 만료 되었을 경우 재발급
     * Redis Server 에서 refresh Token 을 가져옴
     */

    public AccessTokenResponse accessTokenByRefreshToken(String accessToken, RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REFRESH_TOKEN);
        }
        String id = jwtTokenProvider.getPayload(accessToken);
        String data = redisUtil.getData(id);

        if (!data.equals(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REFRESH_TOKEN);
        }

        Token newAccessToken = jwtTokenProvider.createAccessToken(id);

        return new AccessTokenResponse(newAccessToken.getValue());
    }


    /**
     * 로그아웃 시 토큰도 같이 삭제
     */
    @Transactional
    public void logout(String accessToken) {
        String payload = jwtTokenProvider.getPayload(accessToken);
        redisUtil.deleteData(payload);
    }


    /**
     * AccessToken 검증 메서드
     */
    private void accessTokenExtractor(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS_TOKEN);
        }
    }


}
