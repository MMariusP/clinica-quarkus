package org.acme.data.schedulers;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.data.Appointment;
import org.acme.data.AppointmentState;
import org.acme.data.boundry.NotificationResource;
import org.acme.data.repoistory.AppointmentRepository;
import org.acme.data.repoistory.UserRepository;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


@ApplicationScoped
public class AppointmentScheduler {
    private static final Logger LOG = Logger.getLogger(AppointmentScheduler.class);

    @Inject
    AppointmentRepository appointments;
    @Inject
    UserRepository users;
    @Inject
    NotificationResource notifications;

    /**
     * Runs every 60 seconds; skips overlapping execution.
     */
    @Scheduled(every = "30s", concurrentExecution = ConcurrentExecution.SKIP)
    @Transactional
    void processAppointments() {
        LOG.info("Processing appointments");
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // Cancel overdue PENDING
        List<Appointment> toCancel = appointments.find(
                "state = ?1 and startAt <= ?2",
                AppointmentState.PENDING, now
        ).stream().peek(a -> {
            a.setState(AppointmentState.CANCELED);
            notifyFrontend(a);
        }).toList();
            List<Appointment> toStart = appointments.find(
                    "state = ?1 and startAt <= ?2",
                    AppointmentState.APPROVED, now
            ).stream().peek(a -> {
                a.setState(AppointmentState.IN_PROGRESS);
                notifyFrontend(a);
            }).toList();

            List<Appointment> toComplete = appointments.find(
                    "state = ?1 and endTime >= ?2",
                    AppointmentState.IN_PROGRESS, now
            ).list().stream().peek(a -> {
                a.setState(AppointmentState.DONE);
                notifyFrontend(a);
            }).toList();;

        }

    private void notifyFrontend(Appointment appointment) {
        LOG.info("Notifying frontend of appointment: " + appointment);
        notifications.emitAppointmentStarted(
                new NotificationResource.AppointmentStartedNotification(
                        appointment.getId(),
                        appointment.getPatientName(),
                        appointment.getDoctor().getUsername(),
                        appointment.getState().name(),
                        "warning" // small warning marked display
                )
        );
    }
}