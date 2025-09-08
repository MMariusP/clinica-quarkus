package org.acme.data.boundry;

import io.quarkus.security.Authenticated;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.*;
import org.jboss.resteasy.annotations.SseElementType;

@Path("/notifications")
@ApplicationScoped
public class NotificationResource {

    @Inject Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    void init() { broadcaster = sse.newBroadcaster(); }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    @Authenticated
    public void stream(@Context SseEventSink sink) {
        broadcaster.register(sink);
    }

    public void emitAppointmentStarted(AppointmentStartedNotification payload) {
        OutboundSseEvent event = sse.newEventBuilder()
                .name("appointment-started")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(AppointmentStartedNotification.class, payload)
                .build();
        broadcaster.broadcast(event);
    }

    public record AppointmentStartedNotification(
            Long appointmentId,
            String patientName,
            String doctorName,
            String status,
            String severity
    ) {}
}
