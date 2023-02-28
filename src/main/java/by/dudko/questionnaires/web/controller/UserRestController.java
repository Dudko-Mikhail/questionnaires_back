package by.dudko.questionnaires.web.controller;

import by.dudko.questionnaires.dto.MessageRequest;
import by.dudko.questionnaires.dto.PageResponse;
import by.dudko.questionnaires.dto.VerificationDto;
import by.dudko.questionnaires.dto.error.ErrorResponse;
import by.dudko.questionnaires.dto.user.ResetPasswordDto;
import by.dudko.questionnaires.dto.user.UserChangePasswordDto;
import by.dudko.questionnaires.dto.user.UserEditDto;
import by.dudko.questionnaires.dto.user.UserReadDto;
import by.dudko.questionnaires.repository.UserRepository;
import by.dudko.questionnaires.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public PageResponse<UserReadDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("principal.id == #id")
    public UserReadDto findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @PostMapping("/email/verification")
    public ResponseEntity<Object> activateAccount(@RequestBody @Validated VerificationDto verificationDto) {
        return userService.activateAccount(verificationDto) ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().build();
    }

    @PostMapping("/verification-code")
    public boolean isVerificationCodeValid(@RequestBody @Validated VerificationDto verificationDto) {
        return userService.isVerificationCodeValid(verificationDto);
    }

    @PostMapping("/{id}/password")
    @PreAuthorize("principal.id == #id")
    public ResponseEntity<Object> changePassword(@PathVariable long id,
                                                 @RequestBody @Validated UserChangePasswordDto changePasswordDto) {
        return userService.changePassword(id, changePasswordDto) ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body(ErrorResponse.of("Current password is invalid"));
    }

    @PostMapping("/password/recovery")
    public ResponseEntity<Object> resetPassword(@RequestBody @Validated ResetPasswordDto resetPasswordDto) {
        return userService.resetPassword(resetPasswordDto) ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("principal.id == #id")
    public UserReadDto update(@PathVariable long id, @RequestBody @Validated UserEditDto userEditDto) {
        return userService.update(id, userEditDto);
    }

    @PostMapping("/verification-message")
    public void sendVerificationMessage(@RequestBody @Validated MessageRequest messageRequest) {
        this.userService.sendEmailVerificationMessage(messageRequest.getEmail());
    }

    @PostMapping("/reset-password-message")
    public void sendResetPasswordMessage(@RequestBody @Validated MessageRequest messageRequest) {
        this.userService.sendResetPasswordMessage(messageRequest.getEmail());
    }
}
