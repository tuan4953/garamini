package com.garage.user.service;

import com.garage.user.dto.UserRequest;
import com.garage.user.dto.UserResponse;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // 1. CÁC PHƯƠNG THỨC TRA CỨU & QUẢN LÝ (ADMIN)
    // ==========================================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy tài khoản")
                );
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // ==========================================
    // 2. CÁC THAO TÁC CRUD (THÊM, SỬA, XÓA, KHÓA)
    // ==========================================

    @Transactional
    public void saveOrUpdateUser(User userForm) {
        if (userForm.getId() != null) {
            // Trường hợp CẬP NHẬT (SỬA)
            User existingUser = findById(userForm.getId());
            existingUser.setFullName(userForm.getFullName());
            existingUser.setEmail(userForm.getEmail());
            existingUser.setPhone(userForm.getPhone());
            existingUser.setRole(userForm.getRole());

            // Chỉ mã hóa & đổi mật khẩu nếu người dùng nhập mật khẩu mới
            if (userForm.getPassword() != null && !userForm.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userForm.getPassword()));
            }
            userRepository.save(existingUser);
        } else {
            // Trường hợp THÊM MỚI
            userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
            userForm.setEnabled(true); // Mặc định tài khoản mới tạo sẽ hoạt động
            userRepository.save(userForm);
        }
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ==========================================
    // 3. CÁC PHƯƠNG THỨC CHO USER (PROFILE)
    // ==========================================

    @Transactional
    public UserResponse updateProfile(
            String email,
            UserRequest request
    ) {

        User user = findByEmail(email);

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void changePassword(
            String email,
            String newPassword
    ) {

        User user = findByEmail(email);

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

    // Lấy thông tin user theo ID (trả về Entity User)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));
    }
}