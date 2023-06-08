package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item as it " +
            "where " +
            "(upper(it.name) like concat('%', upper(?1), '%') " +
            "or upper(it.description) like concat('%', upper(?1), '%')) " +
            "and it.available = true " +
            "order by it.id")
    List<Item> findText(String text);

    List<Item> findAllByOwnerOrderById(User user);
}
