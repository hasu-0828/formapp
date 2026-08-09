package com.example.formapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Form;
import com.example.formapp.entity.User;

public interface FormRepository extends JpaRepository<Form, Long> {

    List<Form> findByUser(User user);
}