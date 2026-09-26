package com.booking.hotel.dao;

import com.booking.hotel.model.HotelImage;

import java.sql.SQLException;
import java.util.List;

public interface HotelImageDAO {

    boolean create(HotelImage hotelImage) throws SQLException;

    HotelImage findById(long imageId) throws SQLException;

    List<HotelImage> findAll() throws SQLException;

    List<HotelImage> findByHotel(long hotelId) throws SQLException;

    boolean update(HotelImage hotelImage) throws SQLException;

    boolean delete(long imageId) throws SQLException;
}
