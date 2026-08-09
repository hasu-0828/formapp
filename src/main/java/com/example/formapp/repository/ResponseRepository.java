package com.example.formapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Response;

public interface ResponseRepository
        extends JpaRepository<Response, Long> {

    List<Response> findByFormId(Long formId);

}