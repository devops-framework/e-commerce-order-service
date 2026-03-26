package com.homelab.order.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/healthcheck")
public class HealthResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response check() {
        return Response.ok("OK").build();
    }
}
