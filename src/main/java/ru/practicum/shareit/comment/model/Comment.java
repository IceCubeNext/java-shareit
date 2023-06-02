package ru.practicum.shareit.comment.model;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;
}
