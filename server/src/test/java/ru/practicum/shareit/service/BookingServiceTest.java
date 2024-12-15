package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    User owner;
    User booker;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        User ownerModel = new User(1L, "owner", "owner@gmail.com");
        owner = userRepository.save(ownerModel);
        User bookerModel = new User(2L, "booker", "booker@gmail.com");
        booker = userRepository.save(bookerModel);
        Item itemModel = new Item(1L, "item", "description", true, owner, null);
        item = itemRepository.save(itemModel);
        Booking bookingModel = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2),
                item, booker, BookingStatus.APPROVED);
        booking = bookingRepository.save(bookingModel);
    }

    @Test
    void createAndApprovingBookingTest() {
        Item newItem = itemRepository.save(new Item(
                2L, "newItem", "newDescription", true, owner, null));
        BookingCreationDto bookingCreationDto = new BookingCreationDto();
        bookingCreationDto.setItemId(newItem.getId());
        bookingCreationDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreationDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto bookingDto = bookingService.create(bookingCreationDto, booker.getId());

        assertThat(bookingDto, is(notNullValue()));
        assertThat(bookingDto.getId(), is(notNullValue()));
        assertThat(bookingDto.getStart(), equalTo(bookingCreationDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingCreationDto.getEnd()));
        assertThat(bookingDto.getItem().getId(), equalTo(newItem.getId()));
        assertThat(bookingDto.getItem().getDescription(), equalTo(newItem.getDescription()));
        assertThat(bookingDto.getStatus() == BookingStatus.WAITING, equalTo(true));

        BookingDto bookingDtoApproved = bookingService.bookingApprove(owner.getId(), bookingDto.getId(), true);

        assertThat(bookingDtoApproved.getStatus(), equalTo(BookingStatus.APPROVED));

        BookingDto bookingDtoNotApproved = bookingService.bookingApprove(owner.getId(), bookingDto.getId(), false);

        assertThat(bookingDtoNotApproved.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingTest() {
        User otherUser = userRepository.save(new User(3L, "otherUser", "otherUser@gmail.com"));
        BookingDto ownerGetBooking = bookingService.getBookingById(owner.getId(), booking.getId());
        BookingDto bookerGetBooking = bookingService.getBookingById(booker.getId(), booking.getId());

        assertThat(ownerGetBooking, is(notNullValue()));
        assertThat(bookerGetBooking, is(notNullValue()));
        assertThat(ownerGetBooking.getItem().getName(), equalTo(item.getName()));
        assertThat(bookerGetBooking.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(ownerGetBooking.getId(), equalTo(booking.getId()));
        assertThat(bookerGetBooking.getId(), equalTo(booking.getId()));

        try {
            bookingService.getBookingById(otherUser.getId(), booking.getId());
        } catch (ForbiddenException e) {
            assertThat(e, instanceOf(ForbiddenException.class));
            assertThat(e.getMessage(), equalTo("Получение данных о бронировании доступно только владельцу вещи или арендатору."));
        }

    }

    @Test
    void getBookingsByUserIdTest() {
        Item item2 = itemRepository.save(new Item(2L, "itemCurrent", "description2", true, owner, null));
        Item item3 = itemRepository.save(new Item(3L, "itemFuture", "description3", true, owner, null));
        Item item4 = itemRepository.save(new Item(4L, "itemWaiting", "description4", true, owner, null));
        Item item5 = itemRepository.save(new Item(4L, "itemRejected", "description5", true, owner, null));
        Booking bookingCurrent = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item2, booker, BookingStatus.APPROVED));
        Booking bookingFuture = bookingRepository.save(new Booking(3L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), item3, booker, BookingStatus.APPROVED));
        Booking bookingWaiting = bookingRepository.save(new Booking(4L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item4, booker, BookingStatus.WAITING));
        Booking bookingRejected = bookingRepository.save(new Booking(5L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item5, booker, BookingStatus.REJECTED));

        List<BookingDto> bookingsByUserId = bookingService.getBookingsByUserId(booker.getId(), State.ALL);
        assertThat(bookingsByUserId.size(), equalTo(5));
        List<BookingDto> pastBookings = bookingService.getBookingsByUserId(booker.getId(), State.PAST);
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(pastBookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(pastBookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(pastBookings.get(0).getId(), equalTo(booking.getId()));

        List<BookingDto> currentBookings = bookingService.getBookingsByUserId(booker.getId(), State.CURRENT);
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0).getItem().getId(), equalTo(item2.getId()));
        assertThat(currentBookings.get(0).getItem().getName(), equalTo(item2.getName()));
        assertThat(currentBookings.get(0).getId(), equalTo(bookingCurrent.getId()));
        assertThat(currentBookings.get(0).getStart(), equalTo(bookingCurrent.getStart()));

        List<BookingDto> futureBookings = bookingService.getBookingsByUserId(booker.getId(), State.FUTURE);
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.get(0).getItem().getId(), equalTo(item3.getId()));
        assertThat(futureBookings.get(0).getItem().getName(), equalTo(item3.getName()));
        assertThat(futureBookings.get(0).getId(), equalTo(bookingFuture.getId()));
        assertThat(futureBookings.get(0).getStart(), equalTo(bookingFuture.getStart()));

        List<BookingDto> waitingBookings = bookingService.getBookingsByUserId(booker.getId(), State.WAITING);
        assertThat(waitingBookings.size(), equalTo(1));
        assertThat(waitingBookings.get(0).getItem().getId(), equalTo(item4.getId()));
        assertThat(waitingBookings.get(0).getItem().getName(), equalTo(item4.getName()));
        assertThat(waitingBookings.get(0).getId(), equalTo(bookingWaiting.getId()));
        assertThat(waitingBookings.get(0).getStart(), equalTo(bookingWaiting.getStart()));

        List<BookingDto> rejectedBookings = bookingService.getBookingsByUserId(booker.getId(), State.REJECTED);
        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(rejectedBookings.get(0).getItem().getId(), equalTo(item5.getId()));
        assertThat(rejectedBookings.get(0).getItem().getName(), equalTo(item5.getName()));
        assertThat(rejectedBookings.get(0).getId(), equalTo(bookingRejected.getId()));
        assertThat(rejectedBookings.get(0).getStart(), equalTo(bookingRejected.getStart()));
    }

    @Test
    void getBookingsByOwnerId() {
        Item item2 = itemRepository.save(new Item(2L, "itemCurrent", "description2", true, owner, null));
        Item item3 = itemRepository.save(new Item(3L, "itemFuture", "description3", true, owner, null));
        Item item4 = itemRepository.save(new Item(4L, "itemWaiting", "description4", true, owner, null));
        Item item5 = itemRepository.save(new Item(4L, "itemRejected", "description5", true, owner, null));
        Booking bookingCurrent = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item2, booker, BookingStatus.APPROVED));
        Booking bookingFuture = bookingRepository.save(new Booking(3L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), item3, booker, BookingStatus.APPROVED));
        Booking bookingWaiting = bookingRepository.save(new Booking(4L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item4, booker, BookingStatus.WAITING));
        Booking bookingRejected = bookingRepository.save(new Booking(5L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item5, booker, BookingStatus.REJECTED));

        List<BookingDto> bookingsByUserId = bookingService.getBookingsByOwnerId(owner.getId(), State.ALL);
        assertThat(bookingsByUserId.size(), equalTo(5));
        List<BookingDto> pastBookings = bookingService.getBookingsByOwnerId(owner.getId(), State.PAST);
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(pastBookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(pastBookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(pastBookings.get(0).getId(), equalTo(booking.getId()));

        List<BookingDto> currentBookings = bookingService.getBookingsByOwnerId(owner.getId(), State.CURRENT);
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0).getItem().getId(), equalTo(item2.getId()));
        assertThat(currentBookings.get(0).getItem().getName(), equalTo(item2.getName()));
        assertThat(currentBookings.get(0).getId(), equalTo(bookingCurrent.getId()));
        assertThat(currentBookings.get(0).getStart(), equalTo(bookingCurrent.getStart()));

        List<BookingDto> futureBookings = bookingService.getBookingsByOwnerId(owner.getId(), State.FUTURE);
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.get(0).getItem().getId(), equalTo(item3.getId()));
        assertThat(futureBookings.get(0).getItem().getName(), equalTo(item3.getName()));
        assertThat(futureBookings.get(0).getId(), equalTo(bookingFuture.getId()));
        assertThat(futureBookings.get(0).getStart(), equalTo(bookingFuture.getStart()));

        List<BookingDto> waitingBookings = bookingService.getBookingsByOwnerId(owner.getId(), State.WAITING);
        assertThat(waitingBookings.size(), equalTo(1));
        assertThat(waitingBookings.get(0).getItem().getId(), equalTo(item4.getId()));
        assertThat(waitingBookings.get(0).getItem().getName(), equalTo(item4.getName()));
        assertThat(waitingBookings.get(0).getId(), equalTo(bookingWaiting.getId()));
        assertThat(waitingBookings.get(0).getStart(), equalTo(bookingWaiting.getStart()));

        List<BookingDto> rejectedBookings = bookingService.getBookingsByOwnerId(owner.getId(), State.REJECTED);
        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(rejectedBookings.get(0).getItem().getId(), equalTo(item5.getId()));
        assertThat(rejectedBookings.get(0).getItem().getName(), equalTo(item5.getName()));
        assertThat(rejectedBookings.get(0).getId(), equalTo(bookingRejected.getId()));
        assertThat(rejectedBookings.get(0).getStart(), equalTo(bookingRejected.getStart()));
    }

    @Test
    void deleteBookings() {
        assertThat(bookingRepository.findAll().size(), equalTo(1));

        bookingService.deleteBooking(booking.getId());

        assertThat(bookingRepository.findAll().size(), equalTo(0));
    }

}
