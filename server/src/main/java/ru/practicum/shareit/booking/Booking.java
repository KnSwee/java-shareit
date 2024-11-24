package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Сущность бронирования вещи {@link Item}
 */
@Table(name = "bookings")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;

    @JoinColumn(name = "booker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User booker;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status = BookingStatus.WAITING;
}
