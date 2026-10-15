package io.keede.pcm.core.config.security;

import io.keede.pcm.core.domains.user.service.UserQueryService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * 토큰의 사용자 ID와 토큰 키가 일치하는 사용자가 없으면(회원 탈퇴 등) 만료된 토큰으로 보고 거부한다.
 * 실패 시 401로 응답된다.
 */
@Component
public class TokenOwnerValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error EXPIRED_TOKEN =
            new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "만료된 토큰입니다.", null);

    private final UserQueryService userQueryService;

    public TokenOwnerValidator(
            final UserQueryService userQueryService
    ) {
        this.userQueryService = userQueryService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(
            final Jwt jwt
    ) {
        String tokenKey = jwt.getClaimAsString(JwtTokenProvider.TOKEN_KEY_CLAIM);
        if (tokenKey == null || !userQueryService.isValidTokenOwner(Long.valueOf(jwt.getSubject()), tokenKey)) {
            return OAuth2TokenValidatorResult.failure(EXPIRED_TOKEN);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
