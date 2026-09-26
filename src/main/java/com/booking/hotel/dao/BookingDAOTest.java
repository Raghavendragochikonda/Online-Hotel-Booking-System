package com.booking.hotel.dao;

import com.booking.hotel.model.Booking;
import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Room;
import com.booking.hotel.model.User;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class BookingDAOTest {

    public static void main(String[] args) {

        BookingDAO bookingDAO = new BookingDAOImpl();
        UserDAO userDAO = new UserDAOImpl();
        HotelDAO hotelDAO = new HotelDAOImpl();
        RoomDAO roomDAO = new RoomDAOImpl();

        User testUser = null;
        Hotel testHotel = null;
        Room testRoom = null;

        System.out.println("======================================");
        System.out.println("      BOOKING DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE TEMPORARY USER
            testUser = new User(
                    0,
                    "Booking Test User",
                    "bookingtest@example.com",
                    "test123",
                    "9999999999",
                    "CUSTOMER",
                    "ACTIVE"
            );

            boolean userCreated = userDAO.create(testUser);

            if (!userCreated) {
                throw new RuntimeException("Temporary user creation failed");
            }

            System.out.println("TEMPORARY USER: SUCCESS");
            System.out.println("Generated User ID: " + testUser.getUserId());


            // CREATE TEMPORARY HOTEL
            testHotel = new Hotel(
                    0,
                    null,
                    "Booking Test Hotel",
                    "Hotel created for Booking DAO testing",
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


            // CREATE TEMPORARY ROOM
            testRoom = new Room(
                    0,
                    testHotel,
                    "BOOK-101",
                    "DELUXE",
                    2,
                    new BigDecimal("2500.00"),
                    "AVAILABLE"
            );

            boolean roomCreated = roomDAO.create(testRoom);

            if (!roomCreated) {
                throw new RuntimeException("Temporary room creation failed");
            }

            System.out.println("TEMPORARY ROOM: SUCCESS");
            System.out.println("Generated Room ID: " + testRoom.getRoomId());


            // CREATE BOOKING
            Booking booking = new Booking(
                    0,
                    testUser,
                    testHotel,
                    testRoom,
                    Date.valueOf("2026-10-01"),
                    Date.valueOf("2026-10-03"),
                    2,
                    new BigDecimal("5000.00"),
                    "CONFIRMED"
            );

            boolean created = bookingDAO.create(booking);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated Booking ID: " + booking.getBookingId());


            // FIND BY ID
            Booking foundBooking =
                    bookingDAO.findById(booking.getBookingId());

            if (foundBooking == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Booking> bookings =
                    bookingDAO.findAll();

            if (bookings == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total bookings found: " + bookings.size());


            // FIND BY USER
            List<Booking> userBookings =
                    bookingDAO.findByUser(testUser.getUserId());

            if (userBookings == null) {
                throw new RuntimeException("FIND BY USER test failed");
            }

            System.out.println("FIND BY USER: SUCCESS");
            System.out.println(
                    "Bookings for test user: " + userBookings.size()
            );


            // FIND BY HOTEL
            List<Booking> hotelBookings =
                    bookingDAO.findByHotel(testHotel.getHotelId());

            if (hotelBookings == null) {
                throw new RuntimeException("FIND BY HOTEL test failed");
            }

            System.out.println("FIND BY HOTEL: SUCCESS");
            System.out.println(
                    "Bookings for test hotel: " + hotelBookings.size()
            );


            // UPDATE
            booking.setCheckOutDate(
                    Date.valueOf("2026-10-04")
            );

            booking.setGuests(3);

            booking.setTotalAmount(
                    new BigDecimal("7500.00")
            );

            booking.setBookingStatus("CONFIRMED");

            boolean updated =
                    bookingDAO.update(booking);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE BOOKING
            boolean deleted =
                    bookingDAO.delete(booking.getBookingId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY BOOKING DELETE
            Booking deletedBooking =
                    bookingDAO.findById(booking.getBookingId());

            if (deletedBooking != null) {
                throw new RuntimeException(
                        "DELETE verification failed"
                );
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");


            // CLEANUP ROOM
            boolean roomDeleted =
                    roomDAO.delete(testRoom.getRoomId());

            if (!roomDeleted) {
                throw new RuntimeException(
                        "Temporary room cleanup failed"
                );
            }

            System.out.println("TEMPORARY ROOM CLEANUP: SUCCESS");


            // CLEANUP HOTEL
            boolean hotelDeleted =
                    hotelDAO.delete(testHotel.getHotelId());

            if (!hotelDeleted) {
                throw new RuntimeException(
                        "Temporary hotel cleanup failed"
                );
            }

            System.out.println("TEMPORARY HOTEL CLEANUP: SUCCESS");


            // CLEANUP USER
            boolean userDeleted =
                    userDAO.delete(testUser.getUserId());

            if (!userDeleted) {
                throw new RuntimeException(
                        "Temporary user cleanup failed"
                );
            }

            System.out.println("TEMPORARY USER CLEANUP: SUCCESS");


            System.out.println();
            System.out.println("======================================");
            System.out.println(" BOOKING DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("       BOOKING DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();

            // Cleanup booking-related data if something fails
            try {
                if (testRoom != null && testRoom.getRoomId() > 0) {
                    roomDAO.delete(testRoom.getRoomId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testHotel != null && testHotel.getHotelId() > 0) {
                    hotelDAO.delete(testHotel.getHotelId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testUser != null && testUser.getUserId() > 0) {
                    userDAO.delete(testUser.getUserId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }
        }
    }
}
