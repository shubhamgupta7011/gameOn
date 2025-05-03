package com.example.GameOn.service;

import com.example.GameOn.entity.UserDetails.Users;
import com.example.GameOn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl
//        implements UserDetailsService
{

//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Users user = userRepository.findByUserName(username);
//        if (user != null) {
//             return org.springframework.security.core.userdetails.User.builder()
//                    .username(user.getUserName())
//                    .password(user.getPassword())
////                     .roles(user.getRoles().toArray(new String[0]))
//                    .build();
//        }
//        throw new UsernameNotFoundException("Username not found with name :"+username);
//    }
}
