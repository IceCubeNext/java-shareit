package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item as it " +
            "where " +
            "(upper(it.name) like concat('%', upper(?1), '%') " +
            "or upper(it.description) like concat('%', upper(?1), '%')) " +
            "and it.available = true " +
            "order by it.id")
    Page<Item> findText(String text, Pageable page);

    List<Item> findAllByRequestOrderByRequestCreatedDesc(Request request);
    Page<Item> findAllByOwnerOrderById(User user, Pageable page);
    List<Item> findByRequestIn(List<Request> requests, Sort sort);
}
