package com.kenny.sdeappbackend.service;

import com.kenny.sdeappbackend.auth.AuthenticationFacade;
import com.kenny.sdeappbackend.model.User;
import com.kenny.sdeappbackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public User getCurrentUser() {
        String email = authenticationFacade.getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }
}
