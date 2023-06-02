package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requestor;
}
