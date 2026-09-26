package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.Review;
import com.booking.hotel.model.User;

import java.math.BigDecimal;
import java.util.List;

public class ReviewDAOTest {

    public static void main(String[] args) {

        ReviewDAO reviewDAO = new ReviewDAOImpl();
        UserDAO userDAO = new UserDAOImpl();
        HotelDAO hotelDAO = new HotelDAOImpl();

        User testUser = null;
        Hotel testHotel = null;

        System.out.println("======================================");
        System.out.println("       REVIEW DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE TEMPORARY USER
            testUser = new User(
                    0,
                    "Review Test User",
                    "reviewtest_" + System.currentTimeMillis() + "@example.com",
                    "test123",
                    "9999999999",
                    "CUSTOMER",
                    "ACTIVE"
            );

            if (!userDAO.create(testUser)) {
                throw new RuntimeException(
                        "Temporary user creation failed"
                );
            }

            System.out.println("TEMPORARY USER: SUCCESS");
            System.out.println(
                    "Generated User ID: " + testUser.getUserId()
            );


            // CREATE TEMPORARY HOTEL
            testHotel = new Hotel(
                    0,
                    null,
                    "Review Test Hotel",
                    "Hotel created for Review DAO testing",
                    "Test Address",
                    new BigDecimal("4.0"),
                    "WiFi",
                    "ACTIVE"
            );

            if (!hotelDAO.create(testHotel)) {
                throw new RuntimeException(
                        "Temporary hotel creation failed"
                );
            }

            System.out.println("TEMPORARY HOTEL: SUCCESS");
            System.out.println(
                    "Generated Hotel ID: " + testHotel.getHotelId()
            );


            // CREATE REVIEW
            Review review = new Review(
                    0,
                    testUser,
                    testHotel,
                    5,
                    "Excellent test hotel"
            );

            boolean created = reviewDAO.create(review);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println(
                    "Generated Review ID: " + review.getReviewId()
            );


            // FIND BY ID
            Review foundReview =
                    reviewDAO.findById(review.getReviewId());

            if (foundReview == null) {
                throw new RuntimeException(
                        "FIND BY ID test failed"
                );
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<Review> reviews =
                    reviewDAO.findAll();

            if (reviews == null) {
                throw new RuntimeException(
                        "FIND ALL test failed"
                );
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println(
                    "Total reviews found: " + reviews.size()
            );


            // FIND BY HOTEL
            List<Review> hotelReviews =
                    reviewDAO.findByHotel(
                            testHotel.getHotelId()
                    );

            if (hotelReviews == null) {
                throw new RuntimeException(
                        "FIND BY HOTEL test failed"
                );
            }

            System.out.println("FIND BY HOTEL: SUCCESS");
            System.out.println(
                    "Reviews for test hotel: "
                            + hotelReviews.size()
            );


            // FIND BY USER
            List<Review> userReviews =
                    reviewDAO.findByUser(
                            testUser.getUserId()
                    );

            if (userReviews == null) {
                throw new RuntimeException(
                        "FIND BY USER test failed"
                );
            }

            System.out.println("FIND BY USER: SUCCESS");
            System.out.println(
                    "Reviews by test user: "
                            + userReviews.size()
            );


            // UPDATE
            review.setRating(4);
            review.setComment(
                    "Updated review comment"
            );

            boolean updated = reviewDAO.update(review);

            if (!updated) {
                throw new RuntimeException(
                        "UPDATE test failed"
                );
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted =
                    reviewDAO.delete(
                            review.getReviewId()
                    );

            if (!deleted) {
                throw new RuntimeException(
                        "DELETE test failed"
                );
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            Review deletedReview =
                    reviewDAO.findById(
                            review.getReviewId()
                    );

            if (deletedReview != null) {
                throw new RuntimeException(
                        "DELETE verification failed"
                );
            }

            System.out.println(
                    "DELETE VERIFICATION: SUCCESS"
            );


            // CLEANUP HOTEL
            if (!hotelDAO.delete(
                    testHotel.getHotelId())) {

                throw new RuntimeException(
                        "Temporary hotel cleanup failed"
                );
            }

            System.out.println(
                    "TEMPORARY HOTEL CLEANUP: SUCCESS"
            );


            // CLEANUP USER
            if (!userDAO.delete(
                    testUser.getUserId())) {

                throw new RuntimeException(
                        "Temporary user cleanup failed"
                );
            }

            System.out.println(
                    "TEMPORARY USER CLEANUP: SUCCESS"
            );


            System.out.println();
            System.out.println("======================================");
            System.out.println(" REVIEW DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("        REVIEW DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();

            // Cleanup after failure
            try {
                if (testHotel != null
                        && testHotel.getHotelId() > 0) {

                    reviewDAO.findByHotel(
                            testHotel.getHotelId()
                    );

                    hotelDAO.delete(
                            testHotel.getHotelId()
                    );
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }

            try {
                if (testUser != null
                        && testUser.getUserId() > 0) {

                    userDAO.delete(
                            testUser.getUserId()
                    );
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }
        }
    }
}
