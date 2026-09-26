package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Room;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements RoomDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(RoomDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO room
            (hotel_id, room_number, room_type, capacity, base_price, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM room WHERE room_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM room";

    private static final String FIND_BY_HOTEL_SQL =
            "SELECT * FROM room WHERE hotel_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE room
            SET hotel_id = ?,
                room_number = ?,
                room_type = ?,
                capacity = ?,
                base_price = ?,
                status = ?
            WHERE room_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM room WHERE room_id = ?";


    @Override
    public boolean create(Room room) throws SQLException {

        logger.info("Starting room creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, room.getHotel().getHotelId());
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getRoomType());
            ps.setInt(4, room.getCapacity());
            ps.setBigDecimal(5, room.getBasePrice());
            ps.setString(6, room.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        room.setRoomId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Room created successfully. Room ID: {}",
                        room.getRoomId()
                );

                return true;
            }

            logger.warn("Room creation completed but no rows were inserted");
            return false;

        } catch (SQLException e) {

            logger.error("Error while creating room", e);
            throw e;
        }
    }


    @Override
    public Room findById(long roomId) throws SQLException {

        logger.info("Starting find room by ID: {}", roomId);

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Room room = mapRow(rs);

                    logger.info(
                            "Room found successfully. Room ID: {}",
                            roomId
                    );

                    return room;
                }
            }

            logger.info("No room found for ID: {}", roomId);
            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding room by ID: {}",
                    roomId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Room> findAll() throws SQLException {

        logger.info("Starting find all rooms");

        List<Room> rooms = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }

            logger.info(
                    "Find all rooms completed. Total rooms: {}",
                    rooms.size()
            );

            return rooms;

        } catch (SQLException e) {

            logger.error("Error while finding all rooms", e);
            throw e;
        }
    }


    @Override
    public List<Room> findByHotel(long hotelId) throws SQLException {

        logger.info(
                "Starting find rooms by hotel ID: {}",
                hotelId
        );

        List<Room> rooms = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_HOTEL_SQL)) {

            ps.setLong(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find rooms by hotel completed. Hotel ID: {}, Total rooms: {}",
                    hotelId,
                    rooms.size()
            );

            return rooms;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding rooms by hotel ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean update(Room room) throws SQLException {

        logger.info(
                "Starting room update. Room ID: {}",
                room.getRoomId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, room.getHotel().getHotelId());
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getRoomType());
            ps.setInt(4, room.getCapacity());
            ps.setBigDecimal(5, room.getBasePrice());
            ps.setString(6, room.getStatus());
            ps.setLong(7, room.getRoomId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Room updated successfully. Room ID: {}",
                        room.getRoomId()
                );

            } else {

                logger.warn(
                        "No room was updated. Room ID: {}",
                        room.getRoomId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating room. Room ID: {}",
                    room.getRoomId(),
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean delete(long roomId) throws SQLException {

        logger.info(
                "Starting room deletion. Room ID: {}",
                roomId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, roomId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Room deleted successfully. Room ID: {}",
                        roomId
                );

            } else {

                logger.warn(
                        "No room was deleted. Room ID: {}",
                        roomId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting room. Room ID: {}",
                    roomId,
                    e
            );

            throw e;
        }
    }


    private Room mapRow(ResultSet rs) throws SQLException {

        Room room = new Room();

        room.setRoomId(rs.getLong("room_id"));

        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getLong("hotel_id"));
        room.setHotel(hotel);

        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setCapacity(rs.getInt("capacity"));
        room.setBasePrice(rs.getBigDecimal("base_price"));
        room.setStatus(rs.getString("status"));

        return room;
    }
}