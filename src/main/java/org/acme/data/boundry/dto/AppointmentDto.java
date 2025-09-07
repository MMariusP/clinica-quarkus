package org.acme.data.boundry.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.acme.data.AppointmentState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private Long id;

    @NotNull
    private Long doctorId;
    private UserDto doctor;

    @NotNull
    private Long procedureId;
    private ProcedureDto procedure;

    @NotBlank
    @Size(max = 255)
    private String patientName;

    /**
     * Expected as "MM.dd.yyyy HH:mm" in Europe/Bucharest
     */
    @NotBlank
    private String startAt;

    /**
     * Expected as "MM.dd.yyyy HH:mm" in Europe/Bucharest
     */
    private String endAt;

    private AppointmentState state;

    @Override
    public String toString() {
        return "AppointmentDto{" +
                "id=" + id +
                ", doctorId=" + doctorId +
                ", procedureId=" + procedureId +
                ", patientName='" + patientName + '\'' +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", state=" + state +
                '}';
    }
}