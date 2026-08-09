package com.example.formapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.formapp.entity.User;
import com.example.formapp.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password:}")
    private String adminPassword;


    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {

        /*
         * ==========================================
         * hasu 管理者がすでに存在するか確認
         * ==========================================
         */

        if (userRepository.findByUsername("hasu").isPresent()) {

            return;
        }


        /*
         * ==========================================
         * 管理者パスワードが設定されていない場合
         *
         * 安全のため管理者を作成しない
         * ==========================================
         */

        if (adminPassword == null
                || adminPassword.isBlank()) {

            System.out.println(
                    "管理者パスワードが設定されていないため、"
                    + "hasu管理者を作成しませんでした。"
            );

            return;
        }


        /*
         * ==========================================
         * hasu 管理者を作成
         * ==========================================
         */

        User admin = new User();

        admin.setUsername("hasu");

        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setRole("ADMIN");

        userRepository.save(admin);


        System.out.println(
                "管理者アカウント hasu を作成しました。"
        );
    }
}

