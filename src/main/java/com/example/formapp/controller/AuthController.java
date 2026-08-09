package com.example.formapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.formapp.entity.User;
import com.example.formapp.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * ==========================================
     * ログイン画面
     * ==========================================
     */

    @GetMapping("/login")
    public String login() {

        return "login";
    }


    /*
     * ==========================================
     * 新規登録画面
     * ==========================================
     */

    @GetMapping("/signup")
    public String signup() {

        return "signup";
    }


    /*
     * ==========================================
     * 新規登録
     * ==========================================
     */

    @PostMapping("/signup")
    public String signup(
            @RequestParam String username,
            @RequestParam String password) {

        /*
         * ==========================================
         * ユーザー名の重複確認
         * ==========================================
         */

        if (userRepository.findByUsername(username).isPresent()) {

            return "redirect:/signup?error";
        }


        /*
         * ==========================================
         * 一般ユーザーを作成
         *
         * 管理者は自動作成されるため、
         * 新規登録者は必ずUSER
         * ==========================================
         */

        User user = new User();

        user.setUsername(username);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRole("USER");

        userRepository.save(user);


        return "redirect:/login";
    }
}

