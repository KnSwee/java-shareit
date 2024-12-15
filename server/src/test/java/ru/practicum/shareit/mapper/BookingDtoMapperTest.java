package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingDtoMapperTest {

    @Test
    public void testBookingDtoMapper() {

        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                new Item(1L, "item", "descr", true, new User(1L, "owner", "user@gmail.com"), null),
                new User(2L, "booker", "booker@gmail.com"), BookingStatus.APPROVED);

        BookingDto bookingDto = BookingDtoMapper.toBookingDto(booking);

        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());

        Booking mappedBooking = BookingDtoMapper.toBooking(bookingDto);

        assertEquals(mappedBooking.getId(), booking.getId());
        assertEquals(mappedBooking.getItem().getId(), booking.getItem().getId());
        assertEquals(mappedBooking.getStart(), booking.getStart());
        assertEquals(mappedBooking.getEnd(), booking.getEnd());
        assertEquals(mappedBooking.getStatus(), booking.getStatus());

    }

}
