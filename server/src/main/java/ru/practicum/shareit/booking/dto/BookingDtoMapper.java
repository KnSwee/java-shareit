package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDtoMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBooker(UserDtoMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(ItemDtoMapper.toItemDto(booking.getItem()));
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        return bookingDto;
    }

    public static List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> bookingDtoMap = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoMap.add(BookingDtoMapper.toBookingDto(booking));
        }
        return bookingDtoMap;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setBooker(UserDtoMapper.toUser(bookingDto.getBooker()));
        booking.setItem(ItemDtoMapper.toItem(bookingDto.getItem()));
        booking.setStatus(bookingDto.getStatus());
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        return booking;
    }

    public static Booking toBooking(BookingCreationDto creationDto) {
        Booking booking = new Booking();
        booking.setEnd(creationDto.getEnd());
        booking.setStart(creationDto.getStart());
        return booking;
    }


}
