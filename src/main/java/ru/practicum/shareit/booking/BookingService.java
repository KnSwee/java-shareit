package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingCreationDto bookingDto, Long userId);

    BookingDto bookingApprove(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUserId(Long userId, State state);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, State state);

    void deleteBooking(Long bookingId);
}
