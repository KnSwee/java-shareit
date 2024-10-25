package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryItemRepository {

    HashMap<Long, Item> items = new HashMap<>();
    private static long nextId = 1;

    public static Long generateId() {
        return nextId++;
    }

    public Item create(Item item) {
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item get(long id) {
        return items.get(id);
    }

    public void delete(long id) {
        items.remove(id);
    }

    public List<Item> findAll() {
        return items.values().stream().toList();
    }

}
