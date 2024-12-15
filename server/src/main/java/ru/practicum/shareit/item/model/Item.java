package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Сущность арендуемой вещи
 */
@Table(name = "items")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available = Boolean.TRUE;

    @JoinColumn(name = "owner_id")
    @ManyToOne
    @ToString.Exclude
    private User owner;

    @JoinColumn(name = "request_id")
    @OneToOne
    @ToString.Exclude
    private ItemRequest request;
}
