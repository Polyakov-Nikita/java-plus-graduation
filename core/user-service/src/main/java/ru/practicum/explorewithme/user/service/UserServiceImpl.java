package ru.practicum.explorewithme.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.service.UserService;
import ru.practicum.explorewithme.user.entity.User;
import ru.practicum.explorewithme.common.pagination.OffsetPageRequest;
import ru.practicum.explorewithme.common.dto.NewUserRequest;
import ru.practicum.explorewithme.common.dto.UserDto;
import ru.practicum.explorewithme.common.dto.UserShortDto;
import ru.practicum.explorewithme.user.exception.DuplicatedEmailException;
import ru.practicum.explorewithme.user.exception.UserNotFoundException;
import ru.practicum.explorewithme.user.mapper.UserMapper;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserShortDto getUser(Long userId) {
        User result = findUser(userId);
        return UserMapper.toUserShortDto(result);
    }

    private User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = new OffsetPageRequest(from, size);
        List<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }

        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedEmailException(request.getEmail());
        }

        User user = userRepository.save(UserMapper.toUser(request));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        checkUserExists(userId);
        userRepository.deleteById(userId);
    }

    private void checkUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public Boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Map<Long, UserShortDto> getUsersMap(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<User> users = userRepository.findByIdIn(ids);
        checkUsersPresent(ids, users);
        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserMapper::toUserShortDto
                ));
    }

    private void checkUsersPresent(List<Long> ids, List<User> users) {
        Set<Long> foundIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        List<Long> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new UserNotFoundException(missingIds.getFirst());
        }
    }
}
