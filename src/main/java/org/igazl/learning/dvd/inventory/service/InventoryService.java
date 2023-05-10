package org.igazl.learning.dvd.inventory.service;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.igazl.learning.dvd.inventory.dao.InventoryEntity;
import org.igazl.learning.dvd.inventory.rest.model.Inventory;
import org.igazl.learning.dvd.inventory.rest.model.Store;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class InventoryService {

    private static final Logger LOG = Logger.getLogger(InventoryService.class);

    @WithSession
    public Uni<List<Inventory>> getInventories(long filmId) {
        return getInventoriesByFilmId(filmId)
                .map(this::convertInventories);
    }

    private static Uni<List<InventoryEntity>> getInventoriesByFilmId(long filmId) {
        return InventoryEntity
                .findByFilmIdWithActiveRentals(filmId);
    }

    private List<Inventory> convertInventories(List<InventoryEntity> inventories) {
        return inventories
                .stream().map(InventoryService::createInventory)
                .toList();
    }

    private static Inventory createInventory(InventoryEntity inventory) {
        LOG.infov("The inventory {0} has {1} amount of rentals.", inventory.id, inventory.rentals.size());
        return new Inventory(
                inventory.id,
                inventory.filmId,
                new Store(
                        inventory.storeId,
                        "Store " + inventory.storeId
                ),
                inventory.rentals.stream().noneMatch(rental -> {
                    LOG.infov("Inventory {0} rental {1}", inventory.id, rental.id);
                    return rental.returnDate == null;
                })
        );
    }
}
