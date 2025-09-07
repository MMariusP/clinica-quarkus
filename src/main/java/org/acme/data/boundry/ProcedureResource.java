package org.acme.data.boundry;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.data.boundry.dto.ProcedureDto;
import org.acme.data.controller.ProcedureService;

import java.util.List;

@Path("/procedures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcedureResource {

    @Inject
    ProcedureService procedureService;

    @GET
    public List<ProcedureDto> getAll() {
        return procedureService.listAll();
    }

    @GET
    @Path("/{id}")
    public ProcedureDto getById(@PathParam("id") Long id) {
        ProcedureDto p = procedureService.findById(id);
        if (p == null) throw new NotFoundException();
        return p;
    }

    // Optional: create
    @POST
    public ProcedureDto create(ProcedureDto procedureDto) {
        procedureService.create(procedureDto);
        return procedureDto;
    }

}
