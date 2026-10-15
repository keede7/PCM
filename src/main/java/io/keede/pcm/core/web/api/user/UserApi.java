package io.keede.pcm.core.web.api.user;

import io.keede.pcm.core.domains.user.service.UserCommandService;
import io.keede.pcm.core.domains.user.service.UserQueryService;
import io.keede.pcm.core.web.auth.LoginUserId;
import io.keede.pcm.core.web.dto.user.LoginRequest;
import io.keede.pcm.core.web.dto.user.LoginResponse;
import io.keede.pcm.core.web.dto.user.SignupRequest;
import io.keede.pcm.core.web.dto.user.UserResponse;
import io.keede.pcm.core.web.dto.user.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserApi {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UserApi(
            final UserCommandService userCommandService,
            final UserQueryService userQueryService
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(
            @Valid @RequestBody final SignupRequest request
    ) {
        return userCommandService.signup(request.email(), request.password(), request.name());
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody final LoginRequest request
    ) {
        return userQueryService.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public UserResponse me(
            @LoginUserId final Long userId
    ) {
        return userQueryService.getById(userId);
    }

    @PutMapping("/me")
    public UserResponse updateMe(
            @LoginUserId final Long userId,
            @Valid @RequestBody final UserUpdateRequest request
    ) {
        return userCommandService.update(userId, request.name(), request.password());
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(
            @LoginUserId final Long userId
    ) {
        userCommandService.withdraw(userId);
    }
}
