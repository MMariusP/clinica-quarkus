package org.acme.data.repoistory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.data.Appointment;
import org.acme.data.AppointmentState;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class AppointmentRepository implements PanacheRepository<Appointment> {
    public void deleteAppointmentByAppointmentname(String appointmentname) {
    }

    public Set<Long> findBusyDoctorIds(OffsetDateTime reqStart, OffsetDateTime reqEnd, Long excludeAppointmentId) {
        String jpql = """
        select distinct a.doctor.id
        from Appointment a
        where a.state <> :canceled
          and a.startAt < :reqEnd
          and a.endTime   > :reqStart
    """;
        // If your entity fields are named startTime/endTime, change to a.startTime / a.endTime
        // If your PK is appointmentId, change a.id to a.appointmentId below.

        if (excludeAppointmentId != null) {
            jpql += " and a.id <> :excludeId";
        }

        var q = getEntityManager().createQuery(jpql, Long.class)
                .setParameter("canceled", AppointmentState.CANCELED)
                .setParameter("reqStart", reqStart)
                .setParameter("reqEnd",   reqEnd);

        if (excludeAppointmentId != null) q.setParameter("excludeId", excludeAppointmentId);

        return new HashSet<>(q.getResultList());
    }

    public List<Appointment> findByDoctorId(Long doctorId) {
        return list("doctor.id", doctorId);
    }

}
