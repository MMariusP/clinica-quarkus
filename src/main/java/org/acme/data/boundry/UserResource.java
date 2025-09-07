package org.acme.data.boundry;


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
        userService.updateUserByUsername(user, user.getUsername());
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

}
