package org.igazl.learning.dvd.inventory.dao;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventory")
@NamedQuery(name = "Inventory.findByIdWithRental", query = "SELECT i FROM InventoryEntity i LEFT JOIN FETCH i.rentals r WHERE i.filmId = ?1")
@FilterDef(name = "openRentals")
public class InventoryEntity extends PanacheEntityBase {

    @Id
    @Column(name = "inventory_id")
    public Long id;

    public Long filmId;
    public Long storeId;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    @Filter(name = "openRentals", condition = "return_date is null")
    public List<RentalEntity> rentals = List.of();
    public LocalDateTime lastUpdate;

    public static Uni<List<InventoryEntity>> findByFilmIdWithActiveRentals(long filmId) {
        return find("#Inventory.findByIdWithRental", filmId).filter("openRentals").list();
    }
}
