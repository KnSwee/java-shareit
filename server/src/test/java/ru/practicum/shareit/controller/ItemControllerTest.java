package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemWithBookingDateAndCommentsDto itemWithBookingDateAndCommentsDto;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        itemDto = new ItemDto(1L, "Name", "Description", true, null);
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto(
                1L, "Name", "Description", true, null);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Name")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        UpdateItemDto updateItemDto = new UpdateItemDto(
                1L, "Name", "Description", true, null);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Name")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void getItemTest() throws Exception {
        ItemWithBookingDateAndCommentsDto item = new ItemWithBookingDateAndCommentsDto(
                1L, "item", "description", true, null,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(3), null);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));

    }

    @Test
    void findAllUserItemsTest() throws Exception {
        ItemWithBookingDateAndCommentsDto item = new ItemWithBookingDateAndCommentsDto(
                1L, "item", "description", true, null,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(3), null);

        when(itemService.findAllUserItems(anyLong())).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)));

    }

    @Test
    void getItemsToBookTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, null);

        when(itemService.getItemsToBook(any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "any"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L, "good", itemDto, "Vasiliy", LocalDateTime.now().minusDays(1));

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(new CommentCreationDto("good")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("good")))
                .andExpect(jsonPath("$.item.name", is("Name")))
                .andExpect(jsonPath("$.item.description", is("Description")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.authorName", is("Vasiliy")));
    }

}
