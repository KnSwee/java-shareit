package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query("select i from Item i " +
            "where i.available = true and " +
            "(lower(i.name) like concat('%', lower(:text), '%') or " +
            "(lower(i.description) like concat('%', lower(:text), '%')))")
    List<Item> getItemsToBook(@Param("text") String text);

    Boolean existsByOwnerId(Long userId);


}
