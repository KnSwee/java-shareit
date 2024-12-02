package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemPOJO;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select new ru.practicum.shareit.request.model.ItemPOJO(ir, i) " +
            "from Item i " +
            "right join i.request ir " +
            "where ir.requester.id = :userId")
    List<ItemPOJO> getRequestedItems(@Param("userId") Long userId);

    @Query("select new ru.practicum.shareit.request.model.ItemPOJO(ir, i) " +
            "from Item i " +
            "right join i.request ir " +
            "where ir.id = :requestId")
    List<ItemPOJO> getRequestedItemById(@Param("requestId") Long requestId);


}
