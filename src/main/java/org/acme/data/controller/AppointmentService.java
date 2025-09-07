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

import org.acme.data.repoistory.AppointmentRepository;
import org.acme.data.repoistory.ProcedureRepository;
import org.acme.data.repoistory.UserRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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


    private static final ZoneId BUCURESTI = ZoneId.of("Europe/Bucharest");
    private static final DateTimeFormatter IO_FMT =
            DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm", Locale.ROOT);

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

        OffsetDateTime startAt = parseBucharest(dto.getStartAt());
        OffsetDateTime endAt= parseBucharest(dto.getEndAt());

        Appointment entity = new Appointment();
        entity.setDoctor(doctor);
        entity.setProcedure(procedure);
        entity.setPatientName(dto.getPatientName());
        entity.setStartAt(startAt);
        entity.setEndTime(endAt);
        entity.setState(dto.getState());// <— uncomment when end_at exists
        LOG.info("Entity: " + entity.toString());

        appointmentRepo.persist(entity); // no explicit EntityManager

        return toDto(entity);
    }

    // -------------------------
    // Read (by id)
    // -------------------------
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
        return toDto(appt);
    }

    // -------------------------
    // List all (sorted by startAt)
    // -------------------------
    @Transactional(value = TxType.SUPPORTS)
    public List<AppointmentDto> listAll() {
        return appointmentRepo.find(
                "select a from Appointment a " +
                        "join fetch a.procedure " +
                        "join fetch a.doctor " +
                        "order by a.startAt asc"
        ).list().stream().map(this::toDto).toList();
    }

    // -------------------------
    // Update (full replace of editable fields)
    // -------------------------
    @Transactional
    public AppointmentDto update(Long id, @Valid AppointmentDto dto) {
        LOG.info("DTO:" + dto.toString());
        LOG.info("DTO:" + dto.toString());
        LOG.info("DTO:" + dto.toString());

        Appointment appt = appointmentRepo.findById(id);
        LOG.info("DTO:" + dto.toString());
        if (appt == null) throw new NotFoundException("Appointment " + id + " not found");



        User doctor = userRepo.findById(dto.getDoctorId());
        if (doctor == null) throw new NotFoundException("Doctor " + dto.getDoctorId() + " not found");
        Procedure procedure = procedureRepo.findById(dto.getProcedureId());
        if (procedure == null) throw new NotFoundException("Procedure " + dto.getProcedureId() + " not found");

        OffsetDateTime startAt = parseBucharest(dto.getStartAt());
        OffsetDateTime endAt   = parseBucharest(dto.getEndAt());
        appt.setDoctor(doctor);
        appt.setProcedure(procedure);
        appt.setPatientName(dto.getPatientName());
        appt.setStartAt(startAt);
        appt.setEndTime(endAt);
        appt.setState(dto.getState());
        LOG.info("Entity: " + appt.toString());
        appointmentRepo.flush(); // optional

        return toDto(appt);
    }
    @Transactional
    public boolean delete(Long id) {
        boolean deleted = appointmentRepo.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Appointment " + id + " not found");
        }
        return true;
    }

    private AppointmentDto toDto(Appointment a) {
        return AppointmentDto.builder()
                .id(a.getId() == null ? null : a.getId())
                .doctorId(a.getDoctor() != null ? a.getDoctor().getId() : null)
                .procedureId(a.getProcedure() != null ? a.getProcedure().getId() : null)
                .patientName(a.getPatientName())
                .startAt(formatBucharest(a.getStartAt()))
                .state(a.getState())
                .endAt(a.getEndTime() == null ? null : formatBucharest(a.getEndTime()))
                .build();
    }

    // -------------------------
    // Date-time helpers
    // -------------------------
    private static OffsetDateTime parseBucharest(String s) {
        if (isBlank(s)) throw new BadRequestException("startAt must be provided");
        LocalDateTime ldt = LocalDateTime.parse(s.trim(), IO_FMT);
        return ldt.atZone(BUCURESTI).toOffsetDateTime();
    }

    private static String formatBucharest(OffsetDateTime odt) {
        if (odt == null) return null;
        return odt.atZoneSameInstant(BUCURESTI).toLocalDateTime().format(IO_FMT);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Transactional
    public AppointmentDto updateState(Long id, AppointmentDto dto) {
        Appointment appt = appointmentRepo.findById(id);
        if (appt == null) {
            throw new NotFoundException("Appointment not found: " + id);
        }

        // CANCELED is terminal
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

        return toDto(appt);
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
