package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Review;
import com.booking.hotel.model.User;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(ReviewDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO review
            (user_id, hotel_id, rating, comment)
            VALUES (?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM review WHERE review_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM review";

    private static final String FIND_BY_HOTEL_SQL =
            "SELECT * FROM review WHERE hotel_id = ?";

    private static final String FIND_BY_USER_SQL =
            "SELECT * FROM review WHERE user_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE review
            SET user_id = ?,
                hotel_id = ?,
                rating = ?,
                comment = ?
            WHERE review_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM review WHERE review_id = ?";


    @Override
    public boolean create(Review review) throws SQLException {

        logger.info("Starting review creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, review.getUser().getUserId());
            ps.setLong(2, review.getHotel().getHotelId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        review.setReviewId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Review created successfully. Review ID: {}",
                        review.getReviewId()
                );

                return true;
            }

            logger.warn(
                    "Review creation completed but no rows were inserted"
            );

            return false;

        } catch (SQLException e) {

            logger.error("Error while creating review", e);
            throw e;
        }
    }


    @Override
    public Review findById(long reviewId) throws SQLException {

        logger.info(
                "Starting find review by ID: {}",
                reviewId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, reviewId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Review review = mapRow(rs);

                    logger.info(
                            "Review found successfully. Review ID: {}",
                            reviewId
                    );

                    return review;
                }
            }

            logger.info(
                    "No review found for ID: {}",
                    reviewId
            );

            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding review by ID: {}",
                    reviewId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Review> findAll() throws SQLException {

        logger.info("Starting find all reviews");

        List<Review> reviews = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reviews.add(mapRow(rs));
            }

            logger.info(
                    "Find all reviews completed. Total reviews: {}",
                    reviews.size()
            );

            return reviews;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding all reviews",
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Review> findByHotel(long hotelId) throws SQLException {

        logger.info(
                "Starting find reviews by hotel ID: {}",
                hotelId
        );

        List<Review> reviews = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_HOTEL_SQL)) {

            ps.setLong(1, hotelId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find reviews by hotel completed. Hotel ID: {}, Total reviews: {}",
                    hotelId,
                    reviews.size()
            );

            return reviews;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding reviews by hotel ID: {}",
                    hotelId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Review> findByUser(long userId) throws SQLException {

        logger.info(
                "Starting find reviews by user ID: {}",
                userId
        );

        List<Review> reviews = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_USER_SQL)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find reviews by user completed. User ID: {}, Total reviews: {}",
                    userId,
                    reviews.size()
            );

            return reviews;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding reviews by user ID: {}",
                    userId,
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean update(Review review) throws SQLException {

        logger.info(
                "Starting review update. Review ID: {}",
                review.getReviewId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, review.getUser().getUserId());
            ps.setLong(2, review.getHotel().getHotelId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setLong(5, review.getReviewId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Review updated successfully. Review ID: {}",
                        review.getReviewId()
                );

            } else {

                logger.warn(
                        "No review was updated. Review ID: {}",
                        review.getReviewId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating review. Review ID: {}",
                    review.getReviewId(),
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean delete(long reviewId) throws SQLException {

        logger.info(
                "Starting review deletion. Review ID: {}",
                reviewId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, reviewId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Review deleted successfully. Review ID: {}",
                        reviewId
                );

            } else {

                logger.warn(
                        "No review was deleted. Review ID: {}",
                        reviewId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting review. Review ID: {}",
                    reviewId,
                    e
            );

            throw e;
        }
    }


    private Review mapRow(ResultSet rs) throws SQLException {

        Review review = new Review();

        review.setReviewId(rs.getLong("review_id"));

        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        review.setUser(user);

        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getLong("hotel_id"));
        review.setHotel(hotel);

        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));

        return review;
    }
}
