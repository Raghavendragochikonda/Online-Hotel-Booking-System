package com.booking.hotel.dao;

import com.booking.hotel.model.Hotel;
import com.booking.hotel.model.HotelImage;

import java.math.BigDecimal;
import java.util.List;

public class HotelImageDAOTest {

    public static void main(String[] args) {

        HotelImageDAO hotelImageDAO = new HotelImageDAOImpl();
        HotelDAO hotelDAO = new HotelDAOImpl();

        Hotel testHotel = null;

        System.out.println("======================================");
        System.out.println("   HOTEL IMAGE DAO TEST STARTED");
        System.out.println("======================================");

        try {

            // CREATE TEMPORARY HOTEL
            testHotel = new Hotel(
                    0,
                    null,
                    "Image Test Hotel",
                    "Hotel created for Hotel Image DAO testing",
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


            // CREATE HOTEL IMAGE
            HotelImage hotelImage = new HotelImage(
                    0,
                    testHotel,
                    "https://example.com/test-hotel.jpg",
                    "Test hotel image"
            );

            boolean created =
                    hotelImageDAO.create(hotelImage);

            if (!created) {
                throw new RuntimeException(
                        "CREATE test failed"
                );
            }

            System.out.println("CREATE: SUCCESS");
            System.out.println(
                    "Generated Image ID: "
                            + hotelImage.getImageId()
            );


            // FIND BY ID
            HotelImage foundImage =
                    hotelImageDAO.findById(
                            hotelImage.getImageId()
                    );

            if (foundImage == null) {
                throw new RuntimeException(
                        "FIND BY ID test failed"
                );
            }

            System.out.println("FIND BY ID: SUCCESS");


            // FIND ALL
            List<HotelImage> images =
                    hotelImageDAO.findAll();

            if (images == null) {
                throw new RuntimeException(
                        "FIND ALL test failed"
                );
            }

            System.out.println("FIND ALL: SUCCESS");
            System.out.println(
                    "Total images found: " + images.size()
            );


            // FIND BY HOTEL
            List<HotelImage> hotelImages =
                    hotelImageDAO.findByHotel(
                            testHotel.getHotelId()
                    );

            if (hotelImages == null) {
                throw new RuntimeException(
                        "FIND BY HOTEL test failed"
                );
            }

            System.out.println("FIND BY HOTEL: SUCCESS");
            System.out.println(
                    "Images for test hotel: "
                            + hotelImages.size()
            );


            // UPDATE
            hotelImage.setImageUrl(
                    "https://example.com/updated-hotel.jpg"
            );

            hotelImage.setCaption(
                    "Updated test hotel image"
            );

            boolean updated =
                    hotelImageDAO.update(hotelImage);

            if (!updated) {
                throw new RuntimeException(
                        "UPDATE test failed"
                );
            }

            System.out.println("UPDATE: SUCCESS");


            // DELETE
            boolean deleted =
                    hotelImageDAO.delete(
                            hotelImage.getImageId()
                    );

            if (!deleted) {
                throw new RuntimeException(
                        "DELETE test failed"
                );
            }

            System.out.println("DELETE: SUCCESS");


            // VERIFY DELETE
            HotelImage deletedImage =
                    hotelImageDAO.findById(
                            hotelImage.getImageId()
                    );

            if (deletedImage != null) {
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


            System.out.println();
            System.out.println("======================================");
            System.out.println(
                    " HOTEL IMAGE DAO TEST COMPLETED SUCCESSFULLY"
            );
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println(
                    "     HOTEL IMAGE DAO TEST FAILED"
            );
            System.out.println("======================================");

            e.printStackTrace();

            // Cleanup temporary hotel after failure
            try {
                if (testHotel != null
                        && testHotel.getHotelId() > 0) {

                    hotelDAO.delete(
                            testHotel.getHotelId()
                    );
                }
            } catch (Exception cleanupException) {
                cleanupException.printStackTrace();
            }
        }
    }
}
