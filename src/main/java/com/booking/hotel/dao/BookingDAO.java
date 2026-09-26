package com.booking.hotel.dao;

import com.booking.hotel.model.Booking;

import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.time.LocalDate;


public interface BookingDAO {

    boolean create(Booking booking) throws SQLException;
    boolean create(
            Connection connection,
            Booking booking
    ) throws SQLException;

    Booking findById(long bookingId) throws SQLException;

    List<Booking> findAll() throws SQLException;

    List<Booking> findByUser(long userId) throws SQLException;

    List<Booking> findByHotel(long hotelId) throws SQLException;

    boolean update(Booking booking) throws SQLException;

    boolean delete(long bookingId) throws SQLException;

boolean isRoomAvailable(
        long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) throws SQLException;

boolean isRoomAvailable(
        Connection connection,
        long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) throws SQLException;
}
