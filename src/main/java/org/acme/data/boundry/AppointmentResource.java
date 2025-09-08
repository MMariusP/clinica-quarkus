package org.acme.data.boundry;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.acme.data.boundry.dto.AppointmentDto;
import org.acme.data.controller.AppointmentService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {
    @Inject
    AppointmentService appointmentService;

    @Inject
    SecurityIdentity identity;

    @GET
    public List<AppointmentDto> list() {
        return appointmentService.listForCurrentIdentity(identity);
    }
    private Logger logger;
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        var a = appointmentService.findById(id);
        return a == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(a).build();
    }

    @POST
    public Response create(@Valid AppointmentDto appointmentDto) {
        var a = appointmentService.create(appointmentDto);
        return a == null
                ? Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Invalid doctorId or procedureId")).build()
                : Response.created(URI.create("/api/appointments/" + a.getId())).entity(a).build();
    }


    @POST
    @Path("{id}")
    public Response update(@PathParam("id") Long id, @Valid AppointmentDto dto) {
        var a = appointmentService.update(id, dto);
        return a == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(a).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        return appointmentService.delete(id) ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
    @PATCH
    @Path("/{id}/state")
    public Response changeState(@PathParam("id") Long id, @Valid AppointmentDto dto) {
        var updated = appointmentService.updateState(id, dto);
        return Response.ok(updated).build();
    }
}