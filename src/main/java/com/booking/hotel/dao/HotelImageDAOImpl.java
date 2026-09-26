package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.HotelImage;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelImageDAOImpl implements HotelImageDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(HotelImageDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO hotel_image
            (hotel_id, image_url, caption)
            VALUES (?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM hotel_image WHERE image_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM hotel_image";

    private static final String FIND_BY_HOTEL_SQL =
            "SELECT * FROM hotel_image WHERE hotel_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE hotel_image
            SET hotel_id = ?,
                image_url = ?,
                caption = ?
            WHERE image_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM hotel_image WHERE image_id = ?";

    @Override
    public boolean create(HotelImage hotelImage) throws SQLException {

        logger.info("Starting hotel image creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, hotelImage.getHotel().getHotelId());
            ps.setString(2, hotelImage.getImageUrl());
            ps.setString(3, hotelImage.getCaption());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hotelImage.setImageId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Hotel image created successfully. Image ID: {}",
                        hotelImage.getImageId()
                );

                return true;
            }

            logger.warn("Hotel image creation completed but no rows were inserted");
            return false;

        } catch (SQLException e) {

            logger.error("Error while creating hotel image", e);
            throw e;
        }
    }

    @Override
    public HotelImage findById(long imageId) throws SQLException {

        logger.info("Starting find hotel image by ID: {}", imageId);

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, imageId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    HotelImage hotelImage = mapRow(rs);

                    logger.info(
                            "Hotel image found successfully. Image ID: {}",
                            imageId
                    );

                    return hotelImage;
                }
            }

            logger.info("No hotel image found for ID: {}", imageId);
            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding hotel image by ID: {}",
                    imageId,
                    e
            );

            throw e;
        }
    }

    @Override
    public List<HotelImage> findAll() throws SQLException {

        logger.info("Starting find all hotel images");

        List<HotelImage> hotelImages = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hotelImages.add(mapRow(rs));
            }

            logger.info(
                    "Find all hotel images completed. Total images: {}",
                    hotelImages.size()
            );

            return hotelImages;

        } catch (SQLException e) {

            logger.error("Error while finding all hotel images", e);
            throw e;
        }
    }

    @Override
    public List<HotelImage> findByHotel(long hotelId) throws SQLException {

        logger.info(
                "Starting find hotel images by hotel ID: {}",
                hotelId
        );

        List<HotelImage> hotelImages = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_HOTEL_SQL)) {

            ps.setLong(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    hotelImages.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find hotel images by hotel completed. Hotel ID: {}, Total images: {}",
                    hotelId,
                    hotelImages.size()
            );

            return hotelImages;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding hotel images by hotel ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean update(HotelImage hotelImage) throws SQLException {

        logger.info(
                "Starting hotel image update. Image ID: {}",
                hotelImage.getImageId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, hotelImage.getHotel().getHotelId());
            ps.setString(2, hotelImage.getImageUrl());
            ps.setString(3, hotelImage.getCaption());
            ps.setLong(4, hotelImage.getImageId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Hotel image updated successfully. Image ID: {}",
                        hotelImage.getImageId()
                );

            } else {

                logger.warn(
                        "No hotel image was updated. Image ID: {}",
                        hotelImage.getImageId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating hotel image. Image ID: {}",
                    hotelImage.getImageId(),
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean delete(long imageId) throws SQLException {

        logger.info(
                "Starting hotel image deletion. Image ID: {}",
                imageId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, imageId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Hotel image deleted successfully. Image ID: {}",
                        imageId
                );

            } else {

                logger.warn(
                        "No hotel image was deleted. Image ID: {}",
                        imageId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting hotel image. Image ID: {}",
                    imageId,
                    e
            );

            throw e;
        }
    }

    private HotelImage mapRow(ResultSet rs) throws SQLException {

        HotelImage hotelImage = new HotelImage();

        hotelImage.setImageId(
                rs.getLong("image_id")
        );

        Hotel hotel = new Hotel();

        hotel.setHotelId(
                rs.getLong("hotel_id")
        );

        hotelImage.setHotel(hotel);

        hotelImage.setImageUrl(
                rs.getString("image_url")
        );

        hotelImage.setCaption(
                rs.getString("caption")
        );

        return hotelImage;
    }
}