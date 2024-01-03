package com.github.lemongrab32.registrationtest.service;

import com.github.lemongrab32.registrationtest.dtos.RegistrationUserDto;
import com.github.lemongrab32.registrationtest.repository.UserRepository;
import com.github.lemongrab32.registrationtest.repository.entities.Role;
import com.github.lemongrab32.registrationtest.repository.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        String.format("User '%s' not found", username)
                ));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role ->
                        new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    public User save(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setLogin(registrationUserDto.getUsername());
        user.setMail(registrationUserDto.getEmail());
        user.setPassword(registrationUserDto.getPassword());
        roleService.addRole("ROLE_USER", user);
        return userRepository.save(user);
    }
}
