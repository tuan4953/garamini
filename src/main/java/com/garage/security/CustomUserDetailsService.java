package com.garage.security;

import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy email: " + email));
        String generatedHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("123456");
        System.out.println("==================================================");
        System.out.println(">>> EMAIL DÙNG ĐĂNG NHẬP: " + email);
        System.out.println(">>> HASH BCRYPT CHUẨN DÙNG CHO SQL: " + generatedHash);
        System.out.println("==================================================");
        return new CustomUserDetails(user);
    }
}