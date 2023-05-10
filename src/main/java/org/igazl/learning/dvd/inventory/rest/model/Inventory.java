package org.igazl.learning.dvd.inventory.rest.model;

public record Inventory(long id, long filmId, Store store, boolean available) {
}
