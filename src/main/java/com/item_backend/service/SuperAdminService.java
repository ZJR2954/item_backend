package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SuperAdminService {

    boolean addSchoolAdmin(User schoolAdmin) throws JsonProcessingException;

    Map editUser(User user) throws JsonProcessingException;
}
