package org.acme.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clinic_appointments")
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "appointment_procedure_id", nullable = false)
    public Procedure procedure;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_doctor_id", nullable = false)
    private User doctor;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name="state", nullable = false)
    private AppointmentState state;

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", procedure=" + procedure.getName() +
                ", doctor=" + doctor.getUsername() +
                ", patientName='" + patientName + '\'' +
                ", startAt=" + startAt +
                ", endTime=" + endTime +
                ", state=" + state +
                '}';
    }
}
