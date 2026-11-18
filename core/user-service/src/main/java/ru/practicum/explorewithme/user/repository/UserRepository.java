package ru.practicum.explorewithme.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.user.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findAllByIdIn(Collection<Long> ids, Pageable pageable);

    List<User> findByIdIn(List<Long> userIds);
}
