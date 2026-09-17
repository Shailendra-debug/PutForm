package com.skushwaha.getform.Auth.Service;

import com.skushwaha.getform.Auth.DTO.AuthResponse;
import com.skushwaha.getform.Auth.DTO.LoginRequest;
import com.skushwaha.getform.Auth.DTO.PendingRegistration;
import com.skushwaha.getform.Auth.DTO.RegisterRequest;
import com.skushwaha.getform.Auth.JwtUtil;
import com.skushwaha.getform.Email.Service.EmailService;
import com.skushwaha.getform.Email.Service.OtpRateLimitService;
import com.skushwaha.getform.Email.Util.OtpUtil;
import com.skushwaha.getform.Exception.ResourceNotFoundException;
import com.skushwaha.getform.Exception.UserAlreadyExistsException;
import com.skushwaha.getform.Users.Entity.Role;
import com.skushwaha.getform.Users.Entity.User;
import com.skushwaha.getform.Users.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    private final RegistrationRedisService registrationRedisService;

    private final PasswordResetRedisService passwordResetRedisService;

    private final EmailService emailService;

    private final OtpRateLimitService otpRateLimitService;





    @Transactional
    public void register(RegisterRequest request) {

        // ==========================================
        // NORMALIZE EMAIL
        // ==========================================

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);


        // ==========================================
        // CHECK ALREADY REGISTERED
        // ==========================================

        if (userRepository.existsByEmail(email)) {

            throw new UserAlreadyExistsException(
                    "Email already registered"
            );
        }


        // ==========================================
        // RATE LIMIT
        //
        // 5 OTP / 5 MINUTES
        // 30 SECOND COOLDOWN
        // ==========================================

        otpRateLimitService.checkAndRecord(email);


        // ==========================================
        // GENERATE OTP
        // ==========================================

        String otp = OtpUtil.generateOtp();

        // ==========================================
        // CREATE PENDING REGISTRATION
        // ==========================================

        PendingRegistration registration =
                new PendingRegistration(
                        request.name().trim(),
                        email,
                        passwordEncoder.encode(request.password()),passwordEncoder.encode(otp)
                );


        // ==========================================
        // SAVE PENDING REGISTRATION
        // ==========================================

        registrationRedisService.saveRegistration(
                email,
                registration
        );


        // ==========================================
        // HASH OTP
        // ==========================================




        // ==========================================
        // SEND OTP
        // ==========================================

        emailService.sendOtpForRegistration(
                email,
                otp
        );
    }


    public void verifyRegisterOtp(
            String email,
            String otp
    ) {

        email = email
                .trim()
                .toLowerCase(Locale.ROOT);


        // Get OTP hash
        String otpHash =registrationRedisService.getRegistration(email).Otp();

        if (otpHash == null) {

            throw new ResourceNotFoundException(
                    "OTP expired or not found"
            );
        }


        // Verify OTP
        if (otpHash.equals(passwordEncoder.encode(otp))) {
            System.out.println(otp);
            System.out.println(passwordEncoder.encode(otp));
            System.out.println(otpHash);

            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }



        // Get pending registration
        PendingRegistration registration =
                registrationRedisService
                        .getRegistration(email);

        if (registration == null) {

            throw new RuntimeException(
                    "Registration expired. Please register again."
            );
        }

        registrationRedisService.deleteRegistration(email);


        // Create user
        User user = User.builder()
                .name(registration.name())
                .email(registration.email())
                .username(registration.email())
                .password(registration.password())
                .build();

        userRepository.save(user);


        // Delete pending registration
        registrationRedisService
                .deleteRegistration(email);
    }



    public String login(LoginRequest request) {


        String email = request.email()
                .trim()
                .toLowerCase();
        //System.out.println("Hello from auth Service");

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);
        //System.out.println("Hello from auth Service");

        return jwtUtil.generateToken(userDetails);
    }


    public void sendForgotPasswordOtp(
            String email
    ) {

        email = normalize(email);


        // ==========================================
        // CHECK USER
        // ==========================================

        userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );


        // ==========================================
        // RATE LIMIT
        //
        // 5 OTP / 5 MINUTES
        // 30 SECOND COOLDOWN
        // ==========================================

        otpRateLimitService
                .checkAndRecord(email);


        // ==========================================
        // GENERATE OTP
        // ==========================================

        String otp =
                OtpUtil.generateOtp();


        // ==========================================
        // HASH OTP
        // ==========================================

        String otpHash =
                passwordEncoder.encode(otp);


        // ==========================================
        // SAVE OTP HASH
        // ==========================================

        passwordResetRedisService.saveOtp(
                email,
                otpHash
        );


        // ==========================================
        // SEND EMAIL
        // ==========================================

        emailService.sendOtpForForgotPassword(
                email,
                otp
        );
    }

    public String verifyResetOtp(
            String email,
            String otp
    ) {

        email = normalize(email);


        // ==========================================
        // GET HASH
        // ==========================================

        String otpHash =
                passwordResetRedisService
                        .getOtpHash(email);


        if (otpHash == null) {
            throw new RuntimeException(
                    "Invalid or expired OTP"
            );
        }


        // ==========================================
        // VERIFY OTP
        // ==========================================

        if (!passwordEncoder.matches(
                otp,
                otpHash
        )) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }


        // ==========================================
        // DELETE OTP
        // SINGLE USE
        // ==========================================

        passwordResetRedisService.deleteOtp(
                email
        );


        // ==========================================
        // CREATE RESET TOKEN
        // ==========================================

        return passwordResetRedisService
                .createResetToken(email);
    }

    @Transactional
    public void resetPassword(
            String resetToken,
            String newPassword
    ) {

        String email =
                passwordResetRedisService
                        .getEmailFromResetToken(
                                resetToken
                        );


        if (email == null) {
            throw new RuntimeException(
                    "Invalid or expired reset token"
            );
        }


        // ==========================================
        // FIND USER
        // ==========================================

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // ==========================================
        // UPDATE PASSWORD
        // ==========================================

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);


        // ==========================================
        // DELETE TOKEN
        // SINGLE USE
        // ==========================================

        passwordResetRedisService
                .deleteResetToken(resetToken);
    }



    private String normalize(
            String email
    ) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
