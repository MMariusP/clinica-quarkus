package org.acme.data.boundry.dto;

import java.time.LocalDateTime;

public class AppointmentRequestDTO {
    private LocalDateTime dateTime;
    private Long procedureId;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Long procedureId) {
        this.procedureId = procedureId;
    }
}
