package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Room;

import java.math.BigDecimal;
import java.util.List;

public class RoomDAOTest {

    public static void main(String[] args) {

        RoomDAO roomDAO = new RoomDAOImpl();
        HotelDAO hotelDAO = new HotelDAOImpl();

        System.out.println("======================================");
        System.out.println("       ROOM DAO TEST STARTED");
        System.out.println("======================================");

        Hotel testHotel = null;

        try {

            // CREATE TEMPORARY HOTEL
            testHotel = new Hotel(
                    0,
                    null,
                    "Temporary Test Hotel",
                    "Hotel created for Room DAO testing",
                    "Test Address",
                    new BigDecimal("4.0"),
                    "WiFi",
                    "ACTIVE"
            );

            boolean hotelCreated = hotelDAO.create(testHotel);

            if (!hotelCreated) {
                throw new RuntimeException("Temporary hotel creation failed");
            }

            System.out.println("TEMPORARY HOTEL: SUCCESS");
            System.out.println("Generated Hotel ID: " + testHotel.getHotelId());


            // CREATE ROOM
            Room room = new Room(
                    0,
                    testHotel,
                    "TEST-101",
                    "DELUXE",
                    2,
                    new BigDecimal("2500.00"),
                    "AVAILABLE"
            );

            boolean created = roomDAO.create(room);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated Room ID: " + room.getRoomId());


            // FIND BY ID
            Room foundRoom =
                    roomDAO.findById(room.getRoomId());

            if (foundRoom == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Room> rooms = roomDAO.findAll();

            if (rooms == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total rooms found: " + rooms.size());


            // FIND BY HOTEL
            List<Room> hotelRooms =
                    roomDAO.findByHotel(testHotel.getHotelId());

            if (hotelRooms == null) {
                throw new RuntimeException("FIND BY HOTEL test failed");
            }

            System.out.println("FIND BY HOTEL: SUCCESS");
            System.out.println("Rooms for test hotel: " + hotelRooms.size());


            // UPDATE
            room.setRoomNumber("TEST-102");
            room.setRoomType("SUITE");
            room.setCapacity(3);
            room.setBasePrice(new BigDecimal("3500.00"));

            boolean updated = roomDAO.update(room);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE ROOM
            boolean deleted =
                    roomDAO.delete(room.getRoomId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY ROOM DELETE
            Room deletedRoom =
                    roomDAO.findById(room.getRoomId());

            if (deletedRoom != null) {
                throw new RuntimeException("DELETE verification failed");
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");


            // DELETE TEMPORARY HOTEL
            boolean hotelDeleted =
                    hotelDAO.delete(testHotel.getHotelId());

            if (!hotelDeleted) {
                throw new RuntimeException("Temporary hotel deletion failed");
            }

            System.out.println("TEMPORARY HOTEL CLEANUP: SUCCESS");

            System.out.println();
            System.out.println("======================================");
            System.out.println(" ROOM DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("        ROOM DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();

            // Cleanup hotel if room test failed after hotel creation
            if (testHotel != null && testHotel.getHotelId() > 0) {
                try {
                    hotelDAO.delete(testHotel.getHotelId());
                    System.out.println("Temporary hotel cleanup completed.");
                } catch (Exception cleanupException) {
                    System.out.println("Temporary hotel cleanup failed.");
                    cleanupException.printStackTrace();
                }
            }
        }
    }
}