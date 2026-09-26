package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Location;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAOImpl implements HotelDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(HotelDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO hotel
            (location_id, name, description, address,
             star_rating, amenities, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM hotel WHERE hotel_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM hotel";

    private static final String FIND_BY_LOCATION_SQL =
            "SELECT * FROM hotel WHERE location_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE hotel
            SET location_id = ?,
                name = ?,
                description = ?,
                address = ?,
                star_rating = ?,
                amenities = ?,
                status = ?
            WHERE hotel_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM hotel WHERE hotel_id = ?";


    @Override
    public boolean create(Hotel hotel) throws SQLException {

        logger.info("Starting hotel creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            if (hotel.getLocation() != null) {
                ps.setLong(1, hotel.getLocation().getLocationId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, hotel.getName());
            ps.setString(3, hotel.getDescription());
            ps.setString(4, hotel.getAddress());
            ps.setBigDecimal(5, hotel.getStarRating());
            ps.setString(6, hotel.getAmenities());
            ps.setString(7, hotel.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hotel.setHotelId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Hotel created successfully. Hotel ID: {}",
                        hotel.getHotelId()
                );

                return true;
            }

            logger.warn("Hotel creation completed but no rows were inserted");
            return false;

        } catch (SQLException e) {

            logger.error("Error while creating hotel", e);
            throw e;
        }
    }


    @Override
    public Hotel findById(long hotelId) throws SQLException {

        logger.info("Starting find hotel by ID: {}", hotelId);

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Hotel hotel = mapRow(rs);

                    logger.info(
                            "Hotel found successfully. Hotel ID: {}",
                            hotelId
                    );

                    return hotel;
                }
            }

            logger.info("No hotel found for ID: {}", hotelId);
            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding hotel by ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Hotel> findAll() throws SQLException {

        logger.info("Starting find all hotels");

        List<Hotel> hotels = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hotels.add(mapRow(rs));
            }

            logger.info(
                    "Find all hotels completed. Total hotels: {}",
                    hotels.size()
            );

            return hotels;

        } catch (SQLException e) {

            logger.error("Error while finding all hotels", e);
            throw e;
        }
    }


    @Override
    public List<Hotel> findByLocation(long locationId) throws SQLException {

        logger.info(
                "Starting find hotels by location ID: {}",
                locationId
        );

        List<Hotel> hotels = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_LOCATION_SQL)) {

            ps.setLong(1, locationId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    hotels.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find hotels by location completed. Location ID: {}, Total hotels: {}",
                    locationId,
                    hotels.size()
            );

            return hotels;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding hotels by location ID: {}",
                    locationId,
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean update(Hotel hotel) throws SQLException {

        logger.info(
                "Starting hotel update. Hotel ID: {}",
                hotel.getHotelId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            if (hotel.getLocation() != null) {
                ps.setLong(1, hotel.getLocation().getLocationId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, hotel.getName());
            ps.setString(3, hotel.getDescription());
            ps.setString(4, hotel.getAddress());
            ps.setBigDecimal(5, hotel.getStarRating());
            ps.setString(6, hotel.getAmenities());
            ps.setString(7, hotel.getStatus());
            ps.setLong(8, hotel.getHotelId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Hotel updated successfully. Hotel ID: {}",
                        hotel.getHotelId()
                );

            } else {

                logger.warn(
                        "No hotel was updated. Hotel ID: {}",
                        hotel.getHotelId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating hotel. Hotel ID: {}",
                    hotel.getHotelId(),
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean delete(long hotelId) throws SQLException {

        logger.info(
                "Starting hotel deletion. Hotel ID: {}",
                hotelId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, hotelId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Hotel deleted successfully. Hotel ID: {}",
                        hotelId
                );

            } else {

                logger.warn(
                        "No hotel was deleted. Hotel ID: {}",
                        hotelId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting hotel. Hotel ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }


    private Hotel mapRow(ResultSet rs) throws SQLException {

        Hotel hotel = new Hotel();

        hotel.setHotelId(rs.getLong("hotel_id"));

        long locationId = rs.getLong("location_id");

        if (!rs.wasNull()) {
            Location location = new Location();
            location.setLocationId(locationId);
            hotel.setLocation(location);
        }

        hotel.setName(rs.getString("name"));
        hotel.setDescription(rs.getString("description"));
        hotel.setAddress(rs.getString("address"));
        hotel.setStarRating(rs.getBigDecimal("star_rating"));
        hotel.setAmenities(rs.getString("amenities"));
        hotel.setStatus(rs.getString("status"));

        return hotel;
    }
}