package com.garage.auth.service;

import com.garage.auth.dto.RegisterRequest;
import com.garage.user.model.Role;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng"
            );
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException(
                    "Số điện thoại đã được sử dụng"
            );
        }

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "Mật khẩu xác nhận không khớp"
            );
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Người đăng ký mặc định là CUSTOMER.
        // Không cho người dùng tự đăng ký ADMIN / TECHNICIAN.
        user.setRole(Role.CUSTOMER);

        user.setEnabled(true);

        return userRepository.save(user);
    }
}