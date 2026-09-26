package com.booking.hotel.dao;

import com.booking.hotel.model.Booking;
import com.booking.hotel.model.Payment;
import com.booking.hotel.util.JdbcUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentDAOImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO payment
            (booking_id, amount, payment_status, transaction_ref, paid_at)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM payment WHERE payment_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT * FROM payment";

    private static final String FIND_BY_BOOKING_SQL =
            "SELECT * FROM payment WHERE booking_id = ?";

    private static final String UPDATE_SQL = """
            UPDATE payment
            SET booking_id = ?,
                amount = ?,
                payment_status = ?,
                transaction_ref = ?,
                paid_at = ?
            WHERE payment_id = ?
            """;

    private static final String DELETE_SQL =
            "DELETE FROM payment WHERE payment_id = ?";


    @Override
    public boolean create(Payment payment) throws SQLException {

        logger.info("Starting payment creation");

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, payment.getBooking().getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentStatus());
            ps.setString(4, payment.getTransactionRef());
            ps.setTimestamp(5, payment.getPaidAt());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getLong(1));
                    }
                }

                logger.info(
                        "Payment created successfully. Payment ID: {}",
                        payment.getPaymentId()
                );

                return true;
            }

            logger.warn(
                    "Payment creation completed but no rows were inserted"
            );

            return false;

        } catch (SQLException e) {

            logger.error("Error while creating payment", e);
            throw e;
        }
    }


    @Override
    public Payment findById(long paymentId) throws SQLException {

        logger.info(
                "Starting find payment by ID: {}",
                paymentId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, paymentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Payment payment = mapRow(rs);

                    logger.info(
                            "Payment found successfully. Payment ID: {}",
                            paymentId
                    );

                    return payment;
                }
            }

            logger.info(
                    "No payment found for ID: {}",
                    paymentId
            );

            return null;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding payment by ID: {}",
                    paymentId,
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Payment> findAll() throws SQLException {

        logger.info("Starting find all payments");

        List<Payment> payments = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                payments.add(mapRow(rs));
            }

            logger.info(
                    "Find all payments completed. Total payments: {}",
                    payments.size()
            );

            return payments;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding all payments",
                    e
            );

            throw e;
        }
    }


    @Override
    public List<Payment> findByBooking(long bookingId) throws SQLException {

        logger.info(
                "Starting find payments by booking ID: {}",
                bookingId
        );

        List<Payment> payments = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(FIND_BY_BOOKING_SQL)) {

            ps.setLong(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    payments.add(mapRow(rs));
                }
            }

            logger.info(
                    "Find payments by booking completed. Booking ID: {}, Total payments: {}",
                    bookingId,
                    payments.size()
            );

            return payments;

        } catch (SQLException e) {

            logger.error(
                    "Error while finding payments by booking ID: {}",
                    bookingId,
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean update(Payment payment) throws SQLException {

        logger.info(
                "Starting payment update. Payment ID: {}",
                payment.getPaymentId()
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, payment.getBooking().getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentStatus());
            ps.setString(4, payment.getTransactionRef());
            ps.setTimestamp(5, payment.getPaidAt());
            ps.setLong(6, payment.getPaymentId());

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {

                logger.info(
                        "Payment updated successfully. Payment ID: {}",
                        payment.getPaymentId()
                );

            } else {

                logger.warn(
                        "No payment was updated. Payment ID: {}",
                        payment.getPaymentId()
                );
            }

            return updated;

        } catch (SQLException e) {

            logger.error(
                    "Error while updating payment. Payment ID: {}",
                    payment.getPaymentId(),
                    e
            );

            throw e;
        }
    }


    @Override
    public boolean delete(long paymentId) throws SQLException {

        logger.info(
                "Starting payment deletion. Payment ID: {}",
                paymentId
        );

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, paymentId);

            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {

                logger.info(
                        "Payment deleted successfully. Payment ID: {}",
                        paymentId
                );

            } else {

                logger.warn(
                        "No payment was deleted. Payment ID: {}",
                        paymentId
                );
            }

            return deleted;

        } catch (SQLException e) {

            logger.error(
                    "Error while deleting payment. Payment ID: {}",
                    paymentId,
                    e
            );

            throw e;
        }
    }


    private Payment mapRow(ResultSet rs) throws SQLException {

        Payment payment = new Payment();

        payment.setPaymentId(rs.getLong("payment_id"));

        Booking booking = new Booking();
        booking.setBookingId(rs.getLong("booking_id"));
        payment.setBooking(booking);

        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setTransactionRef(rs.getString("transaction_ref"));
        payment.setPaidAt(rs.getTimestamp("paid_at"));

        return payment;
    }
}