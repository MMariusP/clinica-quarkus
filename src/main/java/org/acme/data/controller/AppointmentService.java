package org.acme.data.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.acme.data.Appointment;
import org.acme.data.AppointmentState;
import org.acme.data.Procedure;
import org.acme.data.User;
import org.acme.data.boundry.dto.AppointmentDto;

import org.acme.data.boundry.dto.Mappers;
import org.acme.data.repoistory.AppointmentRepository;
import org.acme.data.repoistory.ProcedureRepository;
import org.acme.data.repoistory.UserRepository;

import java.time.*;
import java.util.List;
import java.util.Objects;

import org.acme.data.util.ClinicUtil;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppointmentService {

    @Inject
    AppointmentRepository appointmentRepo;
    @Inject
    ProcedureRepository procedureRepo;
    @Inject
    UserRepository userRepo;

    private static final Logger LOG = Logger.getLogger(AppointmentService.class);



    @Transactional
    public AppointmentDto create(@Valid AppointmentDto dto) {
        Objects.requireNonNull(dto, "Appointment recieved should be null!");
        LOG.info("Creating appointment " + dto.toString());
        User doctor = userRepo.findById(dto.getDoctorId());

        if (doctor == null) {
            throw new NotFoundException("Doctor " + dto.getDoctorId() + " not found");
        }
        LOG.info("Doctor: " + doctor.toString());

        Procedure procedure = procedureRepo.findById(dto.getProcedureId());
        if (procedure == null) {
            throw new NotFoundException("Procedure " + dto.getProcedureId() + " not found");
        }
        LOG.info("Procedure: " + procedure.toString());

        OffsetDateTime startAt = ClinicUtil.parseBucharest(dto.getStartAt());
        OffsetDateTime endAt= ClinicUtil.parseBucharest(dto.getEndAt());

        Appointment entity = new Appointment();
        entity.setDoctor(doctor);
        entity.setProcedure(procedure);
        entity.setPatientName(dto.getPatientName());
        entity.setStartAt(startAt);
        entity.setEndTime(endAt);
        entity.setState(dto.getState());// <— uncomment when end_at exists
        LOG.info("Entity: " + entity.toString());

        appointmentRepo.persist(entity); // no explicit EntityManager

        return Mappers.mapToDto(entity);
    }

    @Transactional(value = TxType.SUPPORTS)
    public AppointmentDto findById(Long id) {
        Appointment appt = appointmentRepo.find(
                "select a from Appointment a " +
                        "join fetch a.procedure " +
                        "join fetch a.doctor " +
                        "where a.id = ?1", id
        ).firstResult();

        if (appt == null) {
            throw new NotFoundException("Appointment " + id + " not found");
        }
        return Mappers.mapToDto(appt);
    }

    @Transactional(value = TxType.SUPPORTS)
    public List<AppointmentDto> listAll() {
        return appointmentRepo.find(
                "select a from Appointment a " +
                        "join fetch a.procedure " +
                        "join fetch a.doctor " +
                        "order by a.startAt asc"
        ).list().stream().map(Mappers::mapToDto).toList();
    }

    @Transactional
    public AppointmentDto update(Long id, @Valid AppointmentDto dto) {
        LOG.info("DTO:" + dto.toString());
        LOG.info("DTO:" + dto.toString());
        LOG.info("DTO:" + dto.toString());

        Appointment appointment = appointmentRepo.findById(id);
        LOG.info("DTO:" + dto.toString());
        if (appointment == null) throw new NotFoundException("Appointment " + id + " not found");



        User doctor = userRepo.findById(dto.getDoctorId());
        if (doctor == null) throw new NotFoundException("Doctor " + dto.getDoctorId() + " not found");
        Procedure procedure = procedureRepo.findById(dto.getProcedureId());
        if (procedure == null) throw new NotFoundException("Procedure " + dto.getProcedureId() + " not found");

        OffsetDateTime startAt = ClinicUtil.parseBucharest(dto.getStartAt());
        OffsetDateTime endAt   = ClinicUtil.parseBucharest(dto.getEndAt());
        appointment.setDoctor(doctor);
        appointment.setProcedure(procedure);
        appointment.setPatientName(dto.getPatientName());
        appointment.setStartAt(startAt);
        appointment.setEndTime(endAt);
        appointment.setState(dto.getState());
        LOG.info("Entity: " + appointment.toString());
        appointmentRepo.flush(); // optional

        return Mappers.mapToDto(appointment);
    }

    @Transactional
    public boolean delete(Long id) {
        boolean deleted = appointmentRepo.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Appointment " + id + " not found");
        }
        return true;
    }

    @Transactional
    public AppointmentDto updateState(Long id, AppointmentDto dto) {
        Appointment appt = appointmentRepo.findById(id);
        if (appt == null) {
            throw new NotFoundException("Appointment not found: " + id);
        }

        if (appt.getState() == AppointmentState.CANCELED) {
            throw new BadRequestException("Canceled appointments cannot change state");
        }
        AppointmentState next = dto.getState();
        if (next == null) {
            throw new BadRequestException("state is required");
        }

        if (!isAllowedTransition(appt.getState(), next)) {
            throw new BadRequestException("Invalid transition: " + appt.getState() + " -> " + next);
        }
        appt.setState(next);

        return Mappers.mapToDto(appt);
    }

    private boolean isAllowedTransition(AppointmentState current, AppointmentState next) {
        if (next == AppointmentState.CANCELED && current != AppointmentState.CANCELED) return true;
        return switch (current) {
            case PENDING      -> next == AppointmentState.APPROVED;
            case APPROVED     -> next == AppointmentState.IN_PROGRESS;
            case IN_PROGRESS  -> next == AppointmentState.DONE;
            case DONE, CANCELED -> false;
        };
    }
}
