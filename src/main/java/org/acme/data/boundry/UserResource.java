package org.acme.data.boundry;


import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

import org.acme.data.User;
import org.acme.data.boundry.dto.UserDto;
import org.acme.data.controller.UserService;
import org.acme.data.repoistory.UserRepository;
import org.jboss.logging.Logger;

@Path("/users")
@RolesAllowed({"ADMIN", "doctor"})
public class UserResource {

    @Inject
    UserRepository userRepository;

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Set<UserDto> list() {
        return userService.getAllUsers();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public User get(@PathParam("id") Long id) {
        Log.info("Fetching user by ID: " + id);
        return userRepository.findById(id);
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response add(User user) {
        LOG.info("Adding user: " + user.toString());
        userRepository.persist(user);
        return Response.ok().build();
    }


    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(UserDto user){
        LOG.info("Attempting to updated user to with the information " + user.toString() );
        user =  userService.updateUserByUsername(user, user.getUsername());
        LOG.info("Succesfully updated user to with the information.");
        return Response.ok().build();
    }



    @DELETE
    @Path("delete/{username}")
    @Transactional
    public Response delete(@PathParam("username") String username) {
        LOG.info("Deleting user: " + username);
        userService.deleteUserbyUsername(username);
        return Response.ok().build();
    }
    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response available(@QueryParam("startAt") String startAt,
                              @QueryParam("endAt") String endAt,
                              @QueryParam("excludeId") Long excludeId) {
        return Response.ok(userService.findAvailableDoctors(startAt, endAt, excludeId)).build();
    }
}
