package com.booking.hotel.dao;

import com.booking.hotel.model.User;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(UserDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO `user`
            (full_name, email, password_hash, phone, role, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM `user` WHERE user_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM `user`";

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT * FROM `user` WHERE email = ?";

    private static final String UPDATE_SQL = """
            UPDATE `user`
            SET full_name = ?,
                email = ?,
                password_hash = ?,
                phone = ?,
                role = ?,
                status = ?
            WHERE user_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM `user` WHERE user_id = ?";

    @Override
    public boolean create(User user) throws SQLException {

        logger.info("Starting user creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getLong(1));
                    }
                }

                logger.info(
                        "User created successfully. User ID: {}",
                        user.getUserId()
                );

                return true;
            }

            logger.warn(
                    "User creation completed but no rows were inserted"
            );

            return false;

        } catch (SQLException e) {

            logger.error("Error while creating user", e);
            throw e;
        }
    }

    @Override
    public User findById(long userId) throws SQLException {

        logger.info(
                "Starting find user by ID: {}",
                userId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = mapRow(rs);

                    logger.info(
                            "User found successfully. User ID: {}",
                            userId
                    );

                    return user;
                }
            }

            logger.info(
                    "No user found for ID: {}",
                    userId
            );

            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding user by ID: {}",
                    userId,
                    e
            );

            throw e;
        }
    }

    @Override
    public List<User> findAll() throws SQLException {

        logger.info("Starting find all users");

        List<User> users = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

            logger.info(
                    "Find all users completed. Total users: {}",
                    users.size()
            );

            return users;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding all users",
                    e
            );

            throw e;
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {

        logger.info(
                "Starting find user by email: {}",
                email
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = mapRow(rs);

                    logger.info(
                            "User found successfully by email: {}",
                            email
                    );

                    return user;
                }
            }

            logger.info(
                    "No user found for email: {}",
                    email
            );

            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding user by email: {}",
                    email,
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {

        logger.info(
                "Starting user update. User ID: {}",
                user.getUserId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getStatus());
            ps.setLong(7, user.getUserId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "User updated successfully. User ID: {}",
                        user.getUserId()
                );

            } else {

                logger.warn(
                        "No user was updated. User ID: {}",
                        user.getUserId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating user. User ID: {}",
                    user.getUserId(),
                    e
            );

            throw e;
        }
    }

    @Override
    public boolean delete(long userId) throws SQLException {

        logger.info(
                "Starting user deletion. User ID: {}",
                userId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, userId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "User deleted successfully. User ID: {}",
                        userId
                );

            } else {

                logger.warn(
                        "No user was deleted. User ID: {}",
                        userId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting user. User ID: {}",
                    userId,
                    e
            );

            throw e;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setUserId(
                rs.getLong("user_id")
        );

        user.setFullName(
                rs.getString("full_name")
        );

        user.setEmail(
                rs.getString("email")
        );

        user.setPassword(
                rs.getString("password_hash")
        );

        user.setPhone(
                rs.getString("phone")
        );

        user.setRole(
                rs.getString("role")
        );

        user.setStatus(
                rs.getString("status")
        );

        return user;
    }
}