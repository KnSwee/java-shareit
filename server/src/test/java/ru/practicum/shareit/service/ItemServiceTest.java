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
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class ItemServiceTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    Item item;
    ItemDto itemDto;
    User user;
    User savedUser;
    Item savedItem;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");

        savedUser = userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Item");
        item.setOwner(savedUser);
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setName("Test ItemDto");
        itemDto.setDescription("Test ItemDto");

        savedItem = itemRepository.save(item);
    }

    @Test
    void createItemTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("TestCreate");
        itemCreateDto.setDescription("Test create Item");
        itemCreateDto.setAvailable(true);

        ItemDto createdItem = itemService.create(savedUser.getId(), itemCreateDto);

        assertThat(createdItem.getName(), equalTo("TestCreate"));
        assertThat(createdItem.getDescription(), equalTo("Test create Item"));
        assertThat(createdItem.getAvailable(), equalTo(true));
    }

    @Test
    void updateItemTest() {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("TestUpdate");
        updateItemDto.setDescription("Test update Item");
        updateItemDto.setAvailable(false);

        ItemDto updatedItem = itemService.update(savedUser.getId(), savedItem.getId(), updateItemDto);

        assertThat(updatedItem.getName(), equalTo("TestUpdate"));
        assertThat(updatedItem.getDescription(), equalTo("Test update Item"));
        assertThat(updatedItem.getAvailable(), equalTo(false));
    }

    @Test
    void getItemByIdTest() {
        User booker = userRepository.save(
                new User(2L, "booker", "booker@booker.com"));
        Booking lastBooking = bookingRepository.save(
                new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), savedItem, booker, BookingStatus.APPROVED));
        Booking nextBooking = bookingRepository.save(
                new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), savedItem, booker, BookingStatus.APPROVED));
        Comment comment = commentRepository.save(new Comment(1L, "good", savedItem, booker, LocalDateTime.of(2024, 12, 1, 1, 1, 1)));


        ItemWithBookingDateAndCommentsDto getItem = itemService.getItemById(savedUser.getId(), savedItem.getId());
        assertThat(getItem.getName(), equalTo("Test Item"));
        assertThat(getItem.getDescription(), equalTo("Test Item"));
        assertThat(getItem.getAvailable(), equalTo(true));
        assertThat(getItem.getId(), equalTo(savedItem.getId()));
        assertThat(getItem.getLastBooking(), notNullValue());
        assertThat(getItem.getNextBooking(), notNullValue());
        assertThat(getItem.getComments(), hasSize(1));
    }

    @Test
    void commentsTest() {
        Item newItem = new Item();
        newItem.setName("userItem");
        newItem.setDescription("newItem");
        newItem.setAvailable(true);
        newItem.setOwner(savedUser);
        Item secondItem = itemRepository.save(newItem);
        User booker = userRepository.save(new User(2L, "Booker", "booker@gmail.com"));
        Booking firstBook = new Booking(2L,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                savedItem,
                booker,
                BookingStatus.APPROVED);
        Booking secondBook = new Booking(2L,
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().minusDays(4),
                secondItem,
                booker,
                BookingStatus.APPROVED);
        Booking firstBooking = bookingRepository.save(firstBook);
        Booking secondBooking = bookingRepository.save(secondBook);
        CommentDto comment = itemService.createComment(booker.getId(),
                savedItem.getId(),
                new CommentCreationDto("Good enough!"));
        CommentDto secondComment = itemService.createComment(booker.getId(),
                secondItem.getId(),
                new CommentCreationDto("Good enough too!"));

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size(), equalTo(2));
        assertThat(comments.get(0).getItem().getName(), equalTo(savedItem.getName()));
        assertThat(comments.get(1).getItem().getName(), equalTo(secondItem.getName()));
        assertThat(comments.get(0).getText(), equalTo("Good enough!"));
        assertThat(comments.get(1).getText(), equalTo("Good enough too!"));

        List<CommentDto> commentsByItemId = itemService.getCommentsByItemId(savedItem.getId());

        assertThat(commentsByItemId.size(), equalTo(1));
        assertThat(commentsByItemId.get(0).getItem().getName(), equalTo(savedItem.getName()));
        assertThat(commentsByItemId.get(0).getText(), equalTo("Good enough!"));

        List<CommentDto> commentsByOwnerId = itemService.getCommentsByOwnerId(savedUser.getId());

        assertThat(commentsByOwnerId.size(), equalTo(2));
        assertThat(commentsByOwnerId.get(0).getItem().getName(), equalTo(savedItem.getName()));
        assertThat(commentsByOwnerId.get(1).getItem().getName(), equalTo(secondItem.getName()));
        assertThat(commentsByOwnerId.get(0).getText(), equalTo("Good enough!"));
        assertThat(commentsByOwnerId.get(1).getText(), equalTo("Good enough too!"));
    }


    @Test
    void findAllUserItemsTest() {
        Item newItem = new Item();
        newItem.setName("userItem");
        newItem.setDescription("newItem");
        newItem.setAvailable(true);
        newItem.setOwner(savedUser);
        Item secondItem = itemRepository.save(newItem);

        User booker = userRepository.save(new User(2L, "Booker", "booker@gmail.com"));
        Booking firstBooking = new Booking(1L,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3),
                savedItem,
                booker,
                BookingStatus.APPROVED);
        Booking secondBooking = new Booking(2L,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                secondItem,
                booker,
                BookingStatus.APPROVED);
        Booking firstBook = bookingRepository.save(firstBooking);
        Booking secondBook = bookingRepository.save(secondBooking);
        CommentDto comment = itemService.createComment(booker.getId(),
                secondItem.getId(),
                new CommentCreationDto("Good enough"));

        List<ItemWithBookingDateAndCommentsDto> allUserItems = itemService.findAllUserItems(savedUser.getId());

        System.out.println(userRepository.findAll());
        System.out.println(itemRepository.findAll());
        System.out.println(bookingRepository.findAll());

        assertThat(allUserItems.size(), equalTo(2));
        assertThat(allUserItems.get(0).getName(), equalTo("Test Item"));
        assertThat(allUserItems.get(1).getName(), equalTo("userItem"));
        assertThat(allUserItems.get(0).getDescription(), equalTo("Test Item"));
        assertThat(allUserItems.get(1).getDescription(), equalTo("newItem"));
        assertThat(allUserItems.get(0).getAvailable(), equalTo(true));
        assertThat(allUserItems.get(1).getAvailable(), equalTo(true));
        assertThat(allUserItems.get(0).getId(), equalTo(savedItem.getId()));
        assertThat((allUserItems.get(1).getComments().getFirst()), equalTo(comment));
        assertThat(allUserItems.get(1).getLastBooking().format(DateTimeFormatter.BASIC_ISO_DATE),
                equalTo(secondBook.getEnd().format(DateTimeFormatter.BASIC_ISO_DATE)));
    }

    @Test
    void getItemsToBookTest() {
        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("item2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("item3");
        item3.setAvailable(false);
        item3.setOwner(savedUser);
        Item secondItem = itemRepository.save(item2);
        Item thirdItem = itemRepository.save(item3);
        User booker = userRepository.save(new User(2L, "Booker", "booker@gmail.com"));

        List<ItemDto> items = itemService.getItemsToBook("item");

        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getName(), equalTo("Test Item"));
        assertThat(items.get(1).getName(), equalTo("item2"));
        assertThat(items.get(0).getDescription(), equalTo("Test Item"));
        assertThat(items.get(1).getDescription(), equalTo("item2"));
        assertThat(items.get(0).getAvailable(), equalTo(true));
        assertThat(items.get(1).getAvailable(), equalTo(true));
        assertThat(items.get(0).getId(), equalTo(savedItem.getId()));
        assertThat(items.get(1).getId(), equalTo(secondItem.getId()));
    }


}
