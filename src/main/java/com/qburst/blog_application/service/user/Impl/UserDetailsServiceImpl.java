package com.qburst.blog_application.service.user.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.qburst.blog_application.entity.UserEntity;
import com.qburst.blog_application.security.UserPrincipal;
import com.qburst.blog_application.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntities = userRepository.findByUsername(username);

        if (userEntities.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new UserPrincipal(userEntities.get());
    }
}
