package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "requests")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
    @Column(name = "created_date")
    @NotNull
    private LocalDateTime created;

    @OneToMany(mappedBy = "requestId", fetch = FetchType.LAZY)
    private List<Item> items;
}

