package org.acme.data.boundry.interceptors;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.HttpHeaders;
import org.jboss.logging.Logger;

//@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class QParamFilter implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(QParamFilter.class);

    // If you later add more SSE endpoints, turn this into a Set<String> and check anyMatch(...)
    private static final String SSE_RELATIVE = "notifications/stream";    // relative to /clinic/
    private static final String SSE_ABSOLUTE = "/clinic/notifications/stream";

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (!"GET".equalsIgnoreCase(ctx.getMethod())) return;

        final String relPath = ctx.getUriInfo().getPath();                // e.g. notifications/stream
        final String absPath = ctx.getUriInfo().getRequestUri().getPath();// e.g. /clinic/notifications/stream

        // quick log to confirm what we see at runtime
        System.out.println("[SSE Filter] rel=" + relPath + " abs=" + absPath
                + " auth=" + ctx.getHeaderString(HttpHeaders.AUTHORIZATION));

        final boolean looksLikeSse = relPath.startsWith(SSE_RELATIVE) || absPath.startsWith(SSE_ABSOLUTE);
        LOG.info("looksLikeSse=" + looksLikeSse);
        LOG.info("URI=" + ctx.getUriInfo().getRequestUri().toString());

        if (!looksLikeSse) return;

        final String token = ctx.getUriInfo().getQueryParameters().getFirst("token");
        LOG.info("URI=" + ctx.getUriInfo());

        if (token != null && ctx.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
            ctx.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            System.out.println("[SSE Filter] injected Authorization from ?token");
        }
    }
}