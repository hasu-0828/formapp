package com.example.formapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.formapp.entity.User;
import com.example.formapp.repository.UserRepository;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * ==========================================
     * 管理者画面
     * ==========================================
     */

    @GetMapping("/admin")
    public String admin(
            Model model,
            Authentication authentication) {

        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        /*
         * 現在ログインしているユーザー名
         */

        model.addAttribute(
                "currentUsername",
                authentication.getName()
        );

        return "admin";
    }


    /*
     * ==========================================
     * ユーザー削除
     * ==========================================
     */

    @PostMapping("/admin/user/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userRepository.findById(id)
                        .orElseThrow();


        /*
         * ログイン中の本人は削除できない
         */

        if (user.getUsername()
                .equals(authentication.getName())) {

            return "redirect:/admin";
        }


        /*
         * ユーザー削除
         */

        userRepository.delete(user);

        return "redirect:/admin";
    }


    /*
     * ==========================================
     * 権限変更
     * ==========================================
     */

    @PostMapping("/admin/user/{id}/role")
    public String changeRole(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userRepository.findById(id)
                        .orElseThrow();


        /*
         * ログイン中の本人は
         * 権限変更できない
         */

        if (user.getUsername()
                .equals(authentication.getName())) {

            return "redirect:/admin";
        }


        /*
         * ADMIN ⇔ USER
         */

        if ("ADMIN".equals(user.getRole())) {

            user.setRole("USER");

        } else {

            user.setRole("ADMIN");
        }


        userRepository.save(user);

        return "redirect:/admin";
    }
}