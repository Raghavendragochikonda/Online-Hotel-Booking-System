package com.booking.hotel.dao;

import com.booking.hotel.model.Location;

import java.util.List;

public class LocationDAOTest {

    public static void main(String[] args) {

        LocationDAO locationDAO = new LocationDAOImpl();

        System.out.println("======================================");
        System.out.println("    LOCATION DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE
            Location location = new Location(
                    0,
                    "Test City",
                    "CITY",
                    null
            );

            boolean created = locationDAO.create(location);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated Location ID: " + location.getLocationId());


            // FIND BY ID
            Location foundLocation =
                    locationDAO.findById(location.getLocationId());

            if (foundLocation == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Location> locations = locationDAO.findAll();

            if (locations == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total locations found: " + locations.size());


            // FIND BY TYPE
            List<Location> cityLocations =
                    locationDAO.findByType("CITY");

            if (cityLocations == null) {
                throw new RuntimeException("FIND BY TYPE test failed");
            }

            System.out.println("FIND BY TYPE: SUCCESS");
            System.out.println("CITY locations found: " + cityLocations.size());


            // UPDATE
            location.setName("Updated Test City");

            boolean updated = locationDAO.update(location);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted =
                    locationDAO.delete(location.getLocationId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            Location deletedLocation =
                    locationDAO.findById(location.getLocationId());

            if (deletedLocation != null) {
                throw new RuntimeException("DELETE verification failed");
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");

            System.out.println();
            System.out.println("======================================");
            System.out.println(" LOCATION DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("      LOCATION DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();
        }
    }
}
