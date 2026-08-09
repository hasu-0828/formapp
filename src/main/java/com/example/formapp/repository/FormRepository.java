package com.example.formapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Form;

public interface FormRepository extends JpaRepository<Form, Long> {

}