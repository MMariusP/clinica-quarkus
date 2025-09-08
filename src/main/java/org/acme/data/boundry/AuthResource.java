package org.acme.data.boundry;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.data.repoistory.UserRepository;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    public static record LoginRequest(String username, String password) {}
    public static record TokenResponse(String token, long expiresAtEpochSeconds) {}

    @Inject
    UserRepository userRepository;

    private static final Logger LOG = Logger.getLogger(AuthResource.class);


    @POST
    @Path("/login")
    @Transactional
    public Response login(LoginRequest req) {
        LOG.info("Performing authentication.");

        LOG.info(req.username);
        var user = userRepository.find("username = ?1", req.username())
                .firstResultOptional().orElse(null);
        if (user == null || !req.password().equals(user.password)) { // plain-text check
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Set<String> groups = user.roles.stream().map(r -> r.name).collect(Collectors.toSet());
        Instant exp = Instant.now().plus(1, ChronoUnit.HOURS);

        String token = Jwt.claims()
                .issuer("https://clinic.example")
                .subject(user.username)
                .upn(user.username)
                .groups(groups)                  // Quarkus uses 'groups' for @RolesAllowed
                .expiresAt(exp)
                .sign();
        LOG.info("token: " + token);

        return Response.ok(new TokenResponse(token, exp.getEpochSecond())).build();
    }
}