package org.igazl.learning.dvd.inventory.dao;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
public class RentalEntity extends PanacheEntityBase {

    @Id
    @Column(name = "rental_id")
    public Long id;
    public Long customerId;
    public Long staffId;
    public LocalDateTime rentalDate;
    public LocalDateTime returnDate;
    public LocalDateTime lastUpdate;
}
