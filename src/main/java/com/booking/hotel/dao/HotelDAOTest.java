package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Location;

import java.math.BigDecimal;
import java.util.List;

public class HotelDAOTest {

    public static void main(String[] args) {

        HotelDAO hotelDAO = new HotelDAOImpl();

        System.out.println("======================================");
        System.out.println("      HOTEL DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE
            Hotel hotel = new Hotel(
                    0,
                    null,
                    "Test Hotel",
                    "Test hotel description",
                    "Test Address",
                    new BigDecimal("4.5"),
                    "WiFi, Parking",
                    "ACTIVE"
            );

            boolean created = hotelDAO.create(hotel);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated Hotel ID: " + hotel.getHotelId());


            // FIND BY ID
            Hotel foundHotel =
                    hotelDAO.findById(hotel.getHotelId());

            if (foundHotel == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Hotel> hotels = hotelDAO.findAll();

            if (hotels == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total hotels found: " + hotels.size());


            // FIND BY LOCATION
            List<Hotel> locationHotels =
                    hotelDAO.findByLocation(1);

            if (locationHotels == null) {
                throw new RuntimeException("FIND BY LOCATION test failed");
            }

            System.out.println("FIND BY LOCATION: SUCCESS");


            // UPDATE
            hotel.setName("Updated Test Hotel");
            hotel.setAddress("Updated Test Address");

            boolean updated = hotelDAO.update(hotel);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted =
                    hotelDAO.delete(hotel.getHotelId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            Hotel deletedHotel =
                    hotelDAO.findById(hotel.getHotelId());

            if (deletedHotel != null) {
                throw new RuntimeException("DELETE verification failed");
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");

            System.out.println();
            System.out.println("======================================");
            System.out.println(" HOTEL DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("       HOTEL DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();
        }
    }
}
