# Inventory service

The service is responsible for returning the inventory information of films.

The service is based on the `film-service` and used many functionalities that are configured there already.

* tracing
* metrics
* yaml config
* panache with postgreSQL
* reactive endpoints

## Inventory connection with Rental

The `inventory` is connected to `rental` table via the inventory_id.

When the inventory has **no connected row** in the rental table or **all the rows** has its `return_date` column filled with a date (_in the past_) then the given inventory is available.

When the **inventory has a connected rental** where the `rental_date` _is null_, that means the given inventory is rented at the moment.

## Filters with Panache/Hibernate

The [Inventory](./src/main/java/org/igazl/learning/dvd/inventory/rest/model/Inventory.java) response contains a field, called available which is calculated based on the description above.

To achieve the desired value the [InventoryEntity](src/main/java/org/igazl/learning/dvd/inventory/rest/model/Inventory.java) should contain:

1. a list of all the RentalEntity and **filter** it in the service
   * This would retrieve all the rows from the `rental` table which could be a lot
2. the only RentalEntity where the `rental_date == null`
    * for this an extra condition id required in the join: 
```
from
    inventory i1_0
    left join
        rental r1_0
            on i1_0.inventory_id=r1_0.inventory_id
            and r1_0.return_date is null
```

The 2nd option is preferable, but how to do it.

With the Query or NamedQuery we could write: 
```sql
SELECT i FROM InventoryEntity i LEFT JOIN FETCH i.rentals r ON i.id = r.inventory_id AND r.returnDate is NULL WHERE i.filmId = ?1
```

This will cause an exception: `org.hibernate.query.SemanticException: with-clause not allowed on fetched associations; use filters`

So lets use filters.

Define a filter via `@FilterDef(name = "openRentals")` on the `InventoryEntity`, then it can be used on the associated `rentals` field.

> The current service does not use any parameters at the time of the writing of the README, however it could contain ParamDef annotations in the `parameters` property.
>
> Eg: `@FilterDef(name="studentFilter", parameters={ @ParamDef(name="maxAge", type="integer" ) })`
>
> And use: `@Filter(name="studentFilter", condition=":maxAge >= age")`

```java
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    @Filter(name = "openRentals", condition = "return_date is null")
    public List<RentalEntity> rentals = List.of();
```

Hibernate then generate the query with the right condition in the `join`.

### Don't forget to activate

To actually run the filter it has to be activated when needed:

```java
public static Uni<List<InventoryEntity>> findByFilmIdWithActiveRentals(long filmId) {
    return find("#Inventory.findByIdWithRental", filmId)
        .filter("openRentals") // filter is activated
        .list();
}
```

# Ho to run

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:18182/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/inventory-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

