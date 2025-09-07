package org.acme.data.repoistory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.data.Appointment;

@ApplicationScoped
public class AppointmentRepository implements PanacheRepository<Appointment> {
    public void deleteAppointmentByAppointmentname(String appointmentname) {
    }

}
