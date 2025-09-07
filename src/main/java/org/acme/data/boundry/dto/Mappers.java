package org.acme.data.boundry.dto;

import org.acme.data.Appointment;
import org.acme.data.AppointmentState;
import org.acme.data.Procedure;
import org.acme.data.User;
import org.acme.data.util.ClinicUtil;

import java.time.OffsetDateTime;

public class Mappers {
    public static ProcedureDto mapToDto(Procedure p) {
        return ProcedureDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    public static Procedure mapToModel(Procedure procedure, ProcedureDto procedureDto) {
        if(procedure == null || procedureDto == null) { return procedure; }
        procedure.setName(procedureDto.getName());
        procedure.setDescription(procedureDto.getDescription());
        return procedure;
    }

    public static UserDto mapToDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .password(u.getPassword())
                .email(u.getEmail())
                .build();
    }

    public static User mapToModel(User existing, UserDto dto) {
        if (existing == null || dto == null) return existing;

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setPassword(dto.getPassword());
        return existing;
    }

    public static AppointmentDto mapToDto(Appointment a) {
        return AppointmentDto.builder()
                .id(a.getId() == null ? null : a.getId())
                .doctorId(a.getDoctor() != null ? a.getDoctor().getId() : null)
                .procedureId(a.getProcedure() != null ? a.getProcedure().getId() : null)
                .patientName(a.getPatientName())
                .startAt(ClinicUtil.formatBucharest(a.getStartAt()))
                .state(a.getState())
                .endAt(a.getEndTime() == null ? null : ClinicUtil.formatBucharest(a.getEndTime()))
                .doctor(mapToDto(a.getDoctor()))
                .procedure(mapToDto(a.getProcedure()))
                .build();

    }

    public static Appointment mapToModel(Appointment appointment, AppointmentDto dto) {
        if (appointment == null || dto == null) return appointment;
        appointment.setId(dto.getId());
        appointment.setPatientName(dto.getPatientName());
        appointment.setState(dto.getState());
        appointment.setStartAt(OffsetDateTime.parse(dto.getStartAt()));


       return appointment;
    }

}
