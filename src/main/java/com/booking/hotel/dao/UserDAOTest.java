package com.booking.hotel.dao;

import com.booking.hotel.model.User;

import java.util.List;

public class UserDAOTest {

    public static void main(String[] args) {

        String dbPassword = System.getenv("HOTEL_DB_PASSWORD");

        System.out.println(
                "HOTEL_DB_PASSWORD is set: " +
                        (dbPassword != null && !dbPassword.isEmpty())
        );

        UserDAO userDAO = new UserDAOImpl();

        System.out.println("======================================");
        System.out.println("      USER DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE
            User user = new User(
                    0,
                    "Test User",
                    "testuser_" + System.currentTimeMillis() + "@gmail.com",
                    "test123",
                    "9876543210",
                    "CUSTOMER",
                    "ACTIVE"
            );

            boolean created = userDAO.create(user);

            if (!created) {
                throw new RuntimeException("CREATE test failed");
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println("Generated User ID: " + user.getUserId());


            // FIND BY ID
            User foundUser = userDAO.findById(user.getUserId());

            if (foundUser == null) {
                throw new RuntimeException("FIND BY ID test failed");
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<User> users = userDAO.findAll();

            if (users == null) {
                throw new RuntimeException("FIND ALL test failed");
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println("Total users found: " + users.size());


            // FIND BY EMAIL
            User emailUser = userDAO.findByEmail(user.getEmail());

            if (emailUser == null) {
                throw new RuntimeException("FIND BY EMAIL test failed");
            }

            System.out.println("FIND BY EMAIL: SUCCESS");


            // UPDATE
            user.setFullName("Updated Test User");
            user.setPhone("9999999999");

            boolean updated = userDAO.update(user);

            if (!updated) {
                throw new RuntimeException("UPDATE test failed");
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted = userDAO.delete(user.getUserId());

            if (!deleted) {
                throw new RuntimeException("DELETE test failed");
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            User deletedUser = userDAO.findById(user.getUserId());

            if (deletedUser != null) {
                throw new RuntimeException("DELETE verification failed");
            }

            System.out.println("DELETE VERIFICATION: SUCCESS");

            System.out.println();
            System.out.println("======================================");
            System.out.println(" USER DAO TEST COMPLETED SUCCESSFULLY");
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("       USER DAO TEST FAILED");
            System.out.println("======================================");

            e.printStackTrace();
        }
    }
}
