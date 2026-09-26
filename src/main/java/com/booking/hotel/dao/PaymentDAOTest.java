package com.booking.hotel.dao;

import com.booking.hotel.model.Booking;
import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Payment;
import com.booking.hotel.model.Room;
import com.booking.hotel.model.User;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class PaymentDAOTest {

    public static void main(String[] args) {

        PaymentDAO paymentDAO = new PaymentDAOImpl();
        BookingDAO bookingDAO = new BookingDAOImpl();
        UserDAO userDAO = new UserDAOImpl();
        HotelDAO hotelDAO = new HotelDAOImpl();
        RoomDAO roomDAO = new RoomDAOImpl();

        User testUser = null;
        Hotel testHotel = null;
        Room testRoom = null;
        Booking testBooking = null;

        System.out.println("======================================");
        System.out.println("      PAYMENT DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE TEMPORARY USER
            testUser = new User(
                    0,
                    "Payment Test User",
                    "paymenttest_" + System.currentTimeMillis() + "@example.com",
                    "test123",
                    "9999999999",
                    "CUSTOMER",
                    "ACTIVE"
            );

            if (!userDAO.create(testUser)) {
                throw new RuntimeException("Temporary user creation failed");
            }

            System.out.println("TEMPORARY USER: SUCCESS");


            // CREATE TEMPORARY HOTEL
            testHotel = new Hotel(
                    0,
                    null,
                    "Payment Test Hotel",
                    "Hotel created for Payment DAO testing",
                    "Test Address",
                    new BigDecimal("4.0"),
                    "WiFi",
                    "ACTIVE"
            );

            if (!hotelDAO.create(testHotel)) {
                throw new RuntimeException("Temporary hotel creation failed");
            }

            System.out.println("TEMPORARY HOTEL: SUCCESS");


            // CREATE TEMPORARY ROOM
            testRoom = new Room(
                    0,
                    testHotel,
                    "PAY-101",
                    "DELUXE",
                    2,
                    new BigDecimal("2500.00"),
                    "AVAILABLE"
            );

            if (!roomDAO.create(testRoom)) {
                throw new RuntimeException("Temporary room creation failed");
            }

            System.out.println("TEMPORARY ROOM: SUCCESS");


            // CREATE TEMPORARY BOOKING
            testBooking = new Booking(
                    0,
                    testUser,
                    testHotel,
                    testRoom,
                    Date.valueOf("2026-11-01"),
                    Date.valueOf("2026-11-03"),
                    2,
                    new BigDecimal("5000.00"),
                    "CONFIRMED"
            );

            if (!bookingDAO.create(testBooking)) {
                throw new RuntimeException("Temporary booking creation failed");
            }

            System.out.println("TEMPORARY BOOKING: SUCCESS");


            // CREATE PAYMENT
            Payment payment = new Payment(
                    0,
                    testBooking,
                    new BigDecimal("5000.00"),
                    "SUCCESS",
                    "TEST-TXN-" + System.currentTimeMillis(),
                    new Timestamp(System.currentTimeMillis())
            );

            boolean created = paymentDAO.create(payment);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated Payment ID: " + payment.getPaymentId());


            // FIND BY ID
            Payment foundPayment =
                    paymentDAO.findById(payment.getPaymentId());

            if (foundPayment == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Payment> payments =
                    paymentDAO.findAll();

            if (payments == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total payments found: " + payments.size());


            // FIND BY BOOKING
            List<Payment> bookingPayments =
                    paymentDAO.findByBooking(testBooking.getBookingId());

            if (bookingPayments == null) {
                throw new RuntimeException(
                        "FIND BY BOOKING test failed"
                );
            }

            System.out.println("FIND BY BOOKING: SUCCESS");
            System.out.println(
                    "Payments for test booking: "
                            + bookingPayments.size()
            );


            // UPDATE
            payment.setAmount(new BigDecimal("5500.00"));
            payment.setPaymentStatus("SUCCESS");

            boolean updated = paymentDAO.update(payment);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted =
                    paymentDAO.delete(payment.getPaymentId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            Payment deletedPayment =
                    paymentDAO.findById(payment.getPaymentId());

            if (deletedPayment != null) {
                throw new RuntimeException(
                        "DELETE verification failed"
                );
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");


            // CLEANUP BOOKING
            if (!bookingDAO.delete(testBooking.getBookingId())) {
                throw new RuntimeException(
                        "Temporary booking cleanup failed"
                );
            }

            System.out.println("TEMPORARY BOOKING CLEANUP: SUCCESS");


            // CLEANUP ROOM
            if (!roomDAO.delete(testRoom.getRoomId())) {
                throw new RuntimeException(
                        "Temporary room cleanup failed"
                );
            }

            System.out.println("TEMPORARY ROOM CLEANUP: SUCCESS");


            // CLEANUP HOTEL
            if (!hotelDAO.delete(testHotel.getHotelId())) {
                throw new RuntimeException(
                        "Temporary hotel cleanup failed"
                );
            }

            System.out.println("TEMPORARY HOTEL CLEANUP: SUCCESS");


            // CLEANUP USER
            if (!userDAO.delete(testUser.getUserId())) {
                throw new RuntimeException(
                        "Temporary user cleanup failed"
                );
            }

            System.out.println("TEMPORARY USER CLEANUP: SUCCESS");


            System.out.println();
            System.out.println("======================================");
            System.out.println(" PAYMENT DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("       PAYMENT DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();

            // Cleanup in case of failure
            try {
                if (testBooking != null
                        && testBooking.getBookingId() > 0) {
                    bookingDAO.delete(testBooking.getBookingId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testRoom != null
                        && testRoom.getRoomId() > 0) {
                    roomDAO.delete(testRoom.getRoomId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testHotel != null
                        && testHotel.getHotelId() > 0) {
                    hotelDAO.delete(testHotel.getHotelId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testUser != null
                        && testUser.getUserId() > 0) {
                    userDAO.delete(testUser.getUserId());
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }
        }
    }
}
