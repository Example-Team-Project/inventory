package org.igazl.learning.dvd.inventory.rest;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.igazl.learning.dvd.inventory.rest.model.InventoryResponse;
import org.igazl.learning.dvd.inventory.service.InventoryService;
import org.jboss.resteasy.reactive.RestPath;

@Path("/v1/inventories")
@ApplicationScoped
public class InventoryResource {

    private final InventoryService inventoryService;

    public InventoryResource(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GET
    @Path("/{filmId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<InventoryResponse> getAll(@RestPath("filmId") long filmId) {
        return inventoryService
                .getInventories(filmId)
                .map(InventoryResponse::new);
    }
}
