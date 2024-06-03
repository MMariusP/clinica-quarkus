package org.acme.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "clinic_appointments")
public class Appointments {

    @Id
    @Column(name="appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "appointment_doctor_id")
    public User userAppointmentDoctor;

    @ManyToOne
    @JoinColumn(name = "appointment_procedure_id")
    public Procedure procedure;
}
