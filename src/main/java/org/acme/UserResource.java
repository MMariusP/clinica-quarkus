package org.acme;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;

import org.acme.data.User;
import org.acme.data.repoistory.UserRepository;
import org.jboss.logging.Logger;

@Path("/users")
public class UserResource {

    @Inject
    private UserRepository userRepository;

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Set<User> list(){
        return new HashSet<>(userRepository.listAll());
    }




    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public User get(@PathParam("id") Long id){
        return userRepository.findById(id);
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response add(User user){
        LOG.info("Adding user: " + user.toString());

        userRepository.persist(user);
        return Response.ok().build();
    }
}
