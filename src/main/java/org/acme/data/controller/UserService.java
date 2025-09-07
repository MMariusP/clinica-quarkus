package org.acme.data.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.data.User;
import org.acme.data.boundry.dto.UserDto;
import org.acme.data.repoistory.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class UserService {

    @Inject
    UserRepository userRepository;


    public Set<UserDto> getAllUsers() {
        return userRepository.listAll().stream().map(this::toDto).collect(Collectors.toSet());
    }

    public UserDto updateUserByUsername(UserDto userDto, String username) {
        User existtingUser = userRepository.findByUsername(username);
        if (existtingUser == null) return null;

        existtingUser.email = userDto.getEmail();
        existtingUser.password = userDto.getPassword();
        existtingUser.username = userDto.getUsername();
        userRepository.persist(existtingUser);
        return toDto(existtingUser);
    }

    public User createUser(UserDto userDto) {
        User user = fromDto(userDto);
        userRepository.persist(user);
        return user;
    }

    public void deleteUserbyUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .password(u.getPassword())
                .email(u.getEmail())
                .build();
    }

    private User fromDto(UserDto d) {
        return User.builder()
                .username(d.getUsername())
                .password(d.getPassword())
                .email(d.getEmail())
                .build();
    }
}
