package com.tma.recruit.security.service;


import com.tma.recruit.model.entity.User;
import com.tma.recruit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found in the database"));

        return UserDetailsImpl.build(user);
    }
}