package com.booking.hotel.dao;

import com.booking.hotel.model.Location;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAOImpl implements LocationDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(LocationDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO location
            (name, type, parent_id)
            VALUES (?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM location WHERE location_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM location";

    private static final String FIND_BY_TYPE_SQL =
            "SELECT * FROM location WHERE type = ?";

    private static final String UPDATE_SQL = """
            UPDATE location
            SET name = ?,
                type = ?,
                parent_id = ?
            WHERE location_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM location WHERE location_id = ?";

    @Override
    public boolean create(Location location) throws SQLException {

        logger.info("Starting location creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, location.getName());
            ps.setString(2, location.getType());

            if (location.getParent() != null) {
                ps.setLong(3, location.getParent().getLocationId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        location.setLocationId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Location created successfully. Location ID: {}",
                        location.getLocationId()
                );

                return true;
            }

            logger.warn(
                    "Location creation completed but no rows were inserted"
            );

            return false;

        } catch (SQLException e) {

            logger.error("Error while creating location", e);
            throw e;
        }
    }

    @Override
    public Location findById(long locationId) throws SQLException {

        logger.info(
                "Starting find location by ID: {}",
                locationId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, locationId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Location location = mapRow(rs);

                    logger.info(
                            "Location found successfully. Location ID: {}",
                            locationId
                    );

                    return location;
                }
            }

            logger.info(
                    "No location found for ID: {}",
                    locationId
            );

            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding location by ID: {}",
                    locationId,
                    e
            );

            throw e;
        }
    }

    @Override
    public List<Location> findAll() throws SQLException {

        logger.info("Starting find all locations");

        List<Location> locations = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                locations.add(mapRow(rs));
            }

            logger.info(
                    "Find all locations completed. Total locations: {}",
                    locations.size()
            );

            return locations;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding all locations",
                    e
            );

            throw e;
        }
    }

    @Override
    public List<Location> findByType(String type) throws SQLException {

        logger.info(
                "Starting find locations by type: {}",
                type
        );

        List<Location> locations = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_TYPE_SQL)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    locations.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find locations by type completed. Type: {}, Total locations: {}",
                    type,
                    locations.size()
            );

            return locations;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding locations by type: {}",
                    type,
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean update(Location location) throws SQLException {

        logger.info(
                "Starting location update. Location ID: {}",
                location.getLocationId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, location.getName());
            ps.setString(2, location.getType());

            if (location.getParent() != null) {
                ps.setLong(
                        3,
                        location.getParent().getLocationId()
                );
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setLong(
                    4,
                    location.getLocationId()
            );

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Location updated successfully. Location ID: {}",
                        location.getLocationId()
                );

            } else {

                logger.warn(
                        "No location was updated. Location ID: {}",
                        location.getLocationId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating location. Location ID: {}",
                    location.getLocationId(),
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean delete(long locationId) throws SQLException {

        logger.info(
                "Starting location deletion. Location ID: {}",
                locationId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, locationId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Location deleted successfully. Location ID: {}",
                        locationId
                );

            } else {

                logger.warn(
                        "No location was deleted. Location ID: {}",
                        locationId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting location. Location ID: {}",
                    locationId,
                    e
            );

            throw e;
        }
    }

    private Location mapRow(ResultSet rs) throws SQLException {

        Location location = new Location();

        location.setLocationId(
                rs.getLong("location_id")
        );

        location.setName(
                rs.getString("name")
        );

        location.setType(
                rs.getString("type")
        );

        long parentId = rs.getLong("parent_id");

        if (!rs.wasNull()) {

            Location parent = new Location();
            parent.setLocationId(parentId);

            location.setParent(parent);
        }

        return location;
    }
}