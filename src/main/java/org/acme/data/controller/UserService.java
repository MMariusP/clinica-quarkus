package org.acme.data.controller;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.acme.data.User;
import org.acme.data.boundry.dto.Mappers;
import org.acme.data.boundry.dto.UserDto;
import org.acme.data.repoistory.AppointmentRepository;
import org.acme.data.repoistory.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.acme.data.util.ClinicUtil.parseBucharest;

@ApplicationScoped
@Transactional
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    AppointmentRepository appointmentRepository;

    public Set<UserDto> getAllUsers() {
        return userRepository.listAll().stream().map(Mappers::mapUserToDto).collect(Collectors.toSet());
    }

    public UserDto updateUserByUsername(UserDto userDto, String username) {
        User existtingUser = userRepository.findById(userDto.getId());
        if (existtingUser == null) return null;
        Mappers.mapUserToModel(existtingUser, userDto);
        userRepository.persist(existtingUser);
        return Mappers.mapUserToDto(existtingUser);
    }

    public void deleteUserbyUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }

    public List<UserDto> findAvailableDoctors(String startAt, String endAt, Long excludeId) {
        OffsetDateTime start = parseBucharest(startAt);
        OffsetDateTime end = parseBucharest(endAt);
        if (!end.isAfter(start)) {
            throw new BadRequestException("endAt must be after startAt");
        }

        Set<Long> busy = appointmentRepository.findBusyDoctorIds(start, end, excludeId);
        if (busy.isEmpty()) {
            // For now: all users are doctors
            return userRepository.listAll().stream().map(Mappers::mapUserToDto).collect(Collectors.toList());
        }
        return userRepository.list("id not in ?1", busy).stream().map(Mappers::mapUserToDto).collect(Collectors.toList());
    }
    public User getCurrentUser(SecurityIdentity identity) {
        String principal = identity.getPrincipal().getName();

        User user = userRepository.find("username", principal).firstResult();
        if (user == null) {
            throw new NotFoundException("User not found for principal: " + principal);
        }
        return user;
    }
}
