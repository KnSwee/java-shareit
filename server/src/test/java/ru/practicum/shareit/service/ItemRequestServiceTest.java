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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.external.ItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        user = userRepository.save(new User(null, "Ivan", "ivan228@gmail.com"));
        user2 = userRepository.save(new User(null, "Petr", "petr1337@gmail.com"));

        itemRequest = requestRepository.saveAndFlush(ItemRequest.builder()
                .description("itemRequest")
                .requester(user)
                .build());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        itemRequest2 = requestRepository.saveAndFlush(ItemRequest.builder()
                .description("itemRequest2")
                .requester(user)
                .build());
        item = itemRepository.save(new Item(null, "item", "item_description", true, user2, itemRequest));
        item2 = itemRepository.save(new Item(null, "item2", "item_description2", true, user, null));
    }

    @Test
    void getRequestedItemsTest() {
        System.out.println(requestRepository.findAll());
        assertThat(requestRepository.getRequestedItems(user.getId()), hasSize(2));

    }

    @Test
    void createItemRequestTest() {
        ItemRequestDto created = itemRequestService.createItemRequest(
                ItemRequestDto.builder().description("created").build(), user.getId());

        assertThat(created, notNullValue());
        assertThat(created.getDescription(), is("created"));
        assertThat(created.getRequesterId(), equalTo(userRepository.findById(user.getId()).orElseThrow().getId()));
    }

    @Test
    void getItemRequestsByUserIdTest() {
        List<ItemRequestDto> itemRequestsByUserId = itemRequestService.getItemRequestsByUserId(user.getId());

        assertThat(itemRequestsByUserId, hasSize(2));
        assertThat(itemRequestsByUserId.get(0).getId(), greaterThan(itemRequestsByUserId.get(1).getId()));
        assertThat(itemRequestsByUserId.getFirst().getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(itemRequestsByUserId.get(1).getItems(), hasSize(1));
        assertThat(itemRequestsByUserId.get(1).getItems(), contains(
                new ItemResponse(item.getId(), item.getName(), item.getOwner().getId())));
    }

    @Test
    void getAllItemRequestsTest() {
        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequests(user2.getId());

        System.out.println(itemRequests);

        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests.getFirst().getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(itemRequests.get(1).getId(), equalTo(itemRequest.getId()));
    }

    @Test
    void getItemRequestByIdTest() {
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(itemRequest.getId());

        assertThat(itemRequestDto, notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDto.getRequesterId(), equalTo(itemRequest.getRequester().getId()));

        assertThat(itemRequestDto.getItems(), hasSize(1));
        assertThat(itemRequestDto.getItems(), contains(
                new ItemResponse(item.getId(), item.getName(), item.getOwner().getId())));
    }

}
