package com.homelab.order.rest;

import com.homelab.order.utils.ConfigurationLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/checkout")
public class OrderResource {

    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderDetails(@PathParam("id") String id) { // Đổi tên để rõ nghĩa hơn
        // Lấy Host và Port từ file cấu hình XML
        String host = ConfigurationLoader.getProperty("PRODUCT_SERVICE_HOST", "localhost");
        String port = ConfigurationLoader.getProperty("PRODUCT_SERVICE_PORT", "8888");
        String productBaseUrl = String.format("http://%s:%s/api", host, port);

        Client client = ClientBuilder.newClient();
        try {
            // Xây dựng URL một cách an toàn
            WebTarget productTarget = client.target(productBaseUrl)
                    .path("items")
                    .path(id);

            logger.info("Calling Product Service at: {}", productTarget.getUri());

            String productInfo = productTarget
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            return Response.ok("{\"status\": \"Thành công\", \"productInfo\": " + productInfo + "}").build();
        } catch (ProcessingException e) {
            logger.error("Error connecting to Product Service at URL: {}", productBaseUrl, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"Không thể kết nối đến Product Service\"}").build();
        } finally {
            client.close(); // Luôn đóng client để giải phóng tài nguyên
        }
    }
}