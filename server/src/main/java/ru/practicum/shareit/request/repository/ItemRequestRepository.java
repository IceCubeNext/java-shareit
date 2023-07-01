package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequestorOrderByCreatedDesc(User requestor);

    Page<Request> findAllByRequestorNotOrderByCreatedDesc(User requestor, Pageable page);
}
