
package com.booking.hotel.dao;

import com.booking.hotel.model.Booking;
import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Room;
import com.booking.hotel.model.User;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class BookingDAOImpl implements BookingDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(BookingDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO booking
            (user_id, hotel_id, room_id, check_in_date,
             check_out_date, guests, total_amount, booking_status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM booking WHERE booking_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM booking";

    private static final String FIND_BY_USER_SQL =
            "SELECT * FROM booking WHERE user_id = ?";

    private static final String FIND_BY_HOTEL_SQL =
            "SELECT * FROM booking WHERE hotel_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE booking
            SET user_id = ?,
                hotel_id = ?,
                room_id = ?,
                check_in_date = ?,
                check_out_date = ?,
                guests = ?,
                total_amount = ?,
                booking_status = ?
            WHERE booking_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM booking WHERE booking_id = ?";


    @Override
    public boolean create(Booking booking) throws SQLException {

        logger.info("Starting booking creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, booking.getUser().getUserId());
            ps.setLong(2, booking.getHotel().getHotelId());
            ps.setLong(3, booking.getRoom().getRoomId());
            ps.setDate(4, booking.getCheckInDate());
            ps.setDate(5, booking.getCheckOutDate());
            ps.setInt(6, booking.getGuests());
            ps.setBigDecimal(7, booking.getTotalAmount());
            ps.setString(8, booking.getBookingStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setBookingId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Booking created successfully. Booking ID: {}",
                        booking.getBookingId()
                );

                return true;
            }

            logger.warn("Booking creation completed but no rows were inserted");
            return false;

        } catch (SQLException e) {

            logger.error("Error while creating booking", e);
            throw e;
        }
    }
    @Override
    public boolean create(
            Connection connection,
            Booking booking
    ) throws SQLException {

        logger.info(
                "Starting transaction-aware booking creation. User ID: {}, Room ID: {}",
                booking.getUser().getUserId(),
                booking.getRoom().getRoomId()
        );

        String sql = """
            INSERT INTO booking (
                user_id,
                hotel_id,
                room_id,
                check_in_date,
                check_out_date,
                guests,
                total_amount,
                booking_status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setLong(1, booking.getUser().getUserId());
            statement.setLong(2, booking.getHotel().getHotelId());
            statement.setLong(3, booking.getRoom().getRoomId());
            statement.setDate(4, booking.getCheckInDate());
            statement.setDate(5, booking.getCheckOutDate());
            statement.setInt(6, booking.getGuests());
            statement.setBigDecimal(7, booking.getTotalAmount());
            statement.setString(8, booking.getBookingStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                logger.warn("Booking was not created.");
                return false;
            }

            try (ResultSet resultSet = statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    booking.setBookingId(resultSet.getLong(1));

                    logger.info(
                            "Booking created successfully. Booking ID: {}",
                            booking.getBookingId()
                    );

                    return true;
                }
            }
        }

        logger.warn("Booking creation failed.");
        return false;
    }


    @Override
    public Booking findById(long bookingId) throws SQLException {

        logger.info("Starting find booking by ID: {}", bookingId);

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Booking booking = mapRow(rs);

                    logger.info(
                            "Booking found successfully. Booking ID: {}",
                            bookingId
                    );

                    return booking;
                }
            }

            logger.info("No booking found for ID: {}", bookingId);
            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding booking by ID: {}",
                    bookingId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Booking> findAll() throws SQLException {

        logger.info("Starting find all bookings");

        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

            logger.info(
                    "Find all bookings completed. Total bookings: {}",
                    bookings.size()
            );

            return bookings;

        } catch (SQLException e) {

            logger.error("Error while finding all bookings", e);
            throw e;
        }
    }


    @Override
    public List<Booking> findByUser(long userId) throws SQLException {

        logger.info("Starting find bookings for user ID: {}", userId);

        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_USER_SQL)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    bookings.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find bookings by user completed. User ID: {}, Total bookings: {}",
                    userId,
                    bookings.size()
            );

            return bookings;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding bookings for user ID: {}",
                    userId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Booking> findByHotel(long hotelId) throws SQLException {

        logger.info("Starting find bookings for hotel ID: {}", hotelId);

        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_HOTEL_SQL)) {

            ps.setLong(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    bookings.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find bookings by hotel completed. Hotel ID: {}, Total bookings: {}",
                    hotelId,
                    bookings.size()
            );

            return bookings;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding bookings for hotel ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean update(Booking booking) throws SQLException {

        logger.info(
                "Starting booking update. Booking ID: {}",
                booking.getBookingId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, booking.getUser().getUserId());
            ps.setLong(2, booking.getHotel().getHotelId());
            ps.setLong(3, booking.getRoom().getRoomId());
            ps.setDate(4, booking.getCheckInDate());
            ps.setDate(5, booking.getCheckOutDate());
            ps.setInt(6, booking.getGuests());
            ps.setBigDecimal(7, booking.getTotalAmount());
            ps.setString(8, booking.getBookingStatus());
            ps.setLong(9, booking.getBookingId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Booking updated successfully. Booking ID: {}",
                        booking.getBookingId()
                );

            } else {

                logger.warn(
                        "No booking was updated. Booking ID: {}",
                        booking.getBookingId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating booking. Booking ID: {}",
                    booking.getBookingId(),
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean delete(long bookingId) throws SQLException {

        logger.info("Starting booking deletion. Booking ID: {}", bookingId);

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, bookingId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Booking deleted successfully. Booking ID: {}",
                        bookingId
                );

            } else {

                logger.warn(
                        "No booking was deleted. Booking ID: {}",
                        bookingId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting booking. Booking ID: {}",
                    bookingId,
                    e
            );

            throw e;
        }
    }


    private Booking mapRow(ResultSet rs) throws SQLException {

        Booking booking = new Booking();

        booking.setBookingId(rs.getLong("booking_id"));

        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        booking.setUser(user);

        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getLong("hotel_id"));
        booking.setHotel(hotel);

        Room room = new Room();
        room.setRoomId(rs.getLong("room_id"));
        booking.setRoom(room);

        booking.setCheckInDate(rs.getDate("check_in_date"));
        booking.setCheckOutDate(rs.getDate("check_out_date"));
        booking.setGuests(rs.getInt("guests"));
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setBookingStatus(rs.getString("booking_status"));

        return booking;
    }


    @Override
    public boolean isRoomAvailable(
            long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) throws SQLException {

        logger.info(
                "Checking room availability. Room ID: {}, Check-in: {}, Check-out: {}",
                roomId, checkInDate, checkOutDate
        );

        try (Connection connection = JdbcUtil.getConnection()) {
            return isRoomAvailable(
                    connection,
                    roomId,
                    checkInDate,
                    checkOutDate
            );
        }
    }

    @Override
    public boolean isRoomAvailable(
            Connection connection,
            long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) throws SQLException {

        logger.info(
                "Checking room availability using existing connection. Room ID: {}",
                roomId
        );

        String sql = """
            SELECT COUNT(*)
            FROM booking
            WHERE room_id = ?
              AND booking_status IN ('PENDING', 'CONFIRMED')
              AND check_in_date < ?
              AND check_out_date > ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, roomId);
            statement.setDate(2, Date.valueOf(checkOutDate));
            statement.setDate(3, Date.valueOf(checkInDate));

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    boolean available = resultSet.getInt(1) == 0;

                    logger.info(
                            "Room availability result. Room ID: {}, Available: {}",
                            roomId, available
                    );

                    return available;
                }
            }
        }

        logger.warn("Unable to determine room availability. Room ID: {}", roomId);
        return false;
    }
}