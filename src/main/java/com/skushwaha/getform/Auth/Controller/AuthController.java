package com.skushwaha.getform.Auth.Controller;

import com.skushwaha.getform.Auth.DTO.*;
import com.skushwaha.getform.Auth.Service.AuthService;
import com.skushwaha.getform.Auth.Service.PasswordResetRedisService;

import com.skushwaha.getform.Users.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Value("${jwt.access-expiration}")
    private long expiration;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/verify-register-otp")
    public ResponseEntity<?> registerVerifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {

        authService.verifyRegisterOtp(request.email(),request.otp());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        System.out.println("Hello from cantrolaer");

        String token = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true) // true when using HTTPS in production
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(expiration/1000))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "Welcome back! Login successful."
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "Logout successful."
                )
        );
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {




        authService.sendForgotPasswordOtp(request.email());


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If the email exists, a password reset OTP has been sent."
                )
        );
    }


    // =========================
    // VERIFY OTP
    // =========================



    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {

        String resetToken =
                authService.verifyResetOtp(request.email(),request.otp());


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP verified successfully",
                        "resetToken",
                        resetToken
                )
        );
    }


    // =========================
    // RESET PASSWORD
    // =========================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request.resetToken(),request.newPassword());


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully"
                )
        );
    }

}
