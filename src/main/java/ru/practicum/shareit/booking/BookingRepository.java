package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Map;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("select b.item.id, MAX(b.end) " +
            "from Booking b " +
            "inner join b.item i with i.owner.id = :ownerId " +
            "where b.end < current_timestamp " +
            "group by b.item.id")
    Map<Long, LocalDateTime> findLastBookingsDates(@Param("ownerId") Long ownerId);

    @Query("select b.item.id, MIN(b.start) " +
            "from Booking b " +
            "inner join b.item i with i.owner.id = :ownerId " +
            "where b.start > current_timestamp " +
            "group by b.item.id")
    Map<Long, LocalDateTime> findNextBookingsDates(@Param("ownerId") Long ownerId);

    @Query("select MAX(b.end) " +
            "from Booking b " +
            "inner join b.item i " +
            "where i.id = :itemId " +
            "and current_timestamp > b.end " +
            "group by b.item.id")
    LocalDateTime findLastBookingDateByItemId(@Param("itemId") Long itemId);

    @Query("select MIN(b.start) " +
            "from Booking b " +
            "inner join b.item i " +
            "where i.id = :itemId " +
            "and b.start > current_timestamp " +
            "group by b.item.id")
    LocalDateTime findNextBookingDateByItemId(@Param("itemId") Long itemId);

    @Query("select count(b) > 0 " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.booker.id = :bookerId " +
            "and b.end < current_timestamp")
    Boolean checkBookingExist(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId);

}
