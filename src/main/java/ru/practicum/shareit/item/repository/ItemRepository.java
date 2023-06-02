package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item as it " +
           "where it.name like concat(?1, '%') and it.description like concat(?1, '%')" +
           "order by it.id")
    List<Item> findText(String text);

    List<Item> findAllByOwner(User user);
}
