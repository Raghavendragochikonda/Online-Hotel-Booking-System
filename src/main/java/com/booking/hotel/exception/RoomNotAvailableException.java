package com.booking.hotel.exception;

public class RoomNotAvailableException
        extends HotelBookingException {

    public RoomNotAvailableException(String message) {
        super(message);
    }
}
