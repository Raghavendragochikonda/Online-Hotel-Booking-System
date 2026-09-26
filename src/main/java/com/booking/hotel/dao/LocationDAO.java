package com.booking.hotel.dao;

import com.booking.hotel.model.Location;

import java.sql.SQLException;
import java.util.List;

public interface LocationDAO {

    boolean create(Location location) throws SQLException;

    Location findById(long locationId) throws SQLException;

    List<Location> findAll() throws SQLException;

    List<Location> findByType(String type) throws SQLException;

    boolean update(Location location) throws SQLException;

    boolean delete(long locationId) throws SQLException;
}
