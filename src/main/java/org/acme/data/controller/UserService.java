package org.acme.data.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.acme.data.User;
import org.acme.data.boundry.dto.Mappers;
import org.acme.data.boundry.dto.UserDto;
import org.acme.data.repoistory.AppointmentRepository;
import org.acme.data.repoistory.UserRepository;
import org.acme.data.util.ClinicUtil;

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
        return userRepository.listAll().stream().map(Mappers::mapToDto).collect(Collectors.toSet());
    }

    public UserDto updateUserByUsername(UserDto userDto, String username) {
        User existtingUser = userRepository.findById(userDto.getId());
        if (existtingUser == null) return null;
        Mappers.mapToModel(existtingUser, userDto);
        userRepository.persist(existtingUser);
        return Mappers.mapToDto(existtingUser);
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
            return userRepository.listAll().stream().map(Mappers::mapToDto).collect(Collectors.toList());
        }
        return userRepository.list("id not in ?1", busy).stream().map(Mappers::mapToDto).collect(Collectors.toList());
    }

}
