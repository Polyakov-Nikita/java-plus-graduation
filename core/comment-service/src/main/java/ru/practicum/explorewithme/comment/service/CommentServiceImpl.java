package ru.practicum.explorewithme.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.comment.dto.*;
import ru.practicum.explorewithme.comment.entity.Comment;
import ru.practicum.explorewithme.comment.exception.CommentNotFoundException;
import ru.practicum.explorewithme.comment.exception.UnavailableUpdateCommentException;
import ru.practicum.explorewithme.comment.exception.UserNotFoundException;
import ru.practicum.explorewithme.comment.mapper.CommentMapper;
import ru.practicum.explorewithme.comment.repository.CommentRepository;
import ru.practicum.explorewithme.comment.repository.specification.AdminCommentSearchSpecification;
import ru.practicum.explorewithme.common.dto.EventDto;
import ru.practicum.explorewithme.common.dto.UserShortDto;
import ru.practicum.explorewithme.common.pagination.OffsetPageRequest;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.explorewithme.common.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<CommentShortDto> getComments(long eventId, int from, int size) {
        log.trace("Инициировано получение комментариев с параметрами from={} и size={}", from, size);
        List<Comment> result = commentRepository.findByEventIdWithOffset(eventId, from, size);
        log.debug("Найдено {} категорий", result.size());
        return result.stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();
    }

    @Override
    public List<CommentDto> searchComments(long adminId,
                                           String text,
                                           List<Long> authorsIds,
                                           String rangeStart,
                                           String rangeEnd,
                                           List<Long> eventIds,
                                           List<CommentStatus> states,
                                           int from,
                                           int size) {
        log.trace("Инициирован поиск комментариев");
        Specification<Comment> spec = new AdminCommentSearchSpecification(text, authorsIds, rangeStart, rangeEnd, eventIds, states);
        Pageable pageable = new OffsetPageRequest(from, size);
        Page<Comment> commentPage = commentRepository.findAll(spec, pageable);
        List<Comment> comments = commentPage.getContent();
        log.debug("Получен список комментариев {}", comments);
        if (comments.isEmpty()) {
            return List.of();
        }

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto moderateComment(long adminId, long commentId, ModerationAction action) {
        log.trace("Инициирована модерация комментария с id {} администратором с id {}", commentId, adminId);
        LocalDateTime now = LocalDateTime.now();
        checkUserExistence(adminId);
        UserShortDto admin = userService.getUser(adminId);
        Comment comment = findComment(commentId);

        if (action.equals(ModerationAction.APPROVE)) {
            comment.setText(comment.getTextOnModeration());
            comment.setUpdated(now);
            comment.setStatus(CommentStatus.APPROVED);
        }

        if (action.equals(ModerationAction.REJECT) && comment.getText() == null) {
            comment.setStatus(CommentStatus.REJECTED);
        } else if (action.equals(ModerationAction.REJECT) && comment.getText() != null) {
            comment.setStatus(CommentStatus.APPROVED);
        }

        comment.setModeratorId(admin.getId());
        comment.setModerated(now);
        comment.setTextOnModeration(null);
        Comment result = commentRepository.save(comment);
        log.debug("Комментарий после модерации {}", result);
        return CommentMapper.toCommentDto(result);
    }

    private void checkUserExistence(long userId) {
        boolean isUserExists = userService.existsById(userId);
        if (!isUserExists) {
            throw new UserNotFoundException(userId);
        }
    }

    private Comment findComment(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(long adminId, long commentId) {
        log.trace("Инициировано удаление комментария с id {} администратором с id {}", commentId, adminId);
        checkUserExistence(adminId);
        Comment comment = findComment(commentId);
        commentRepository.delete(comment);
        log.debug("Удален комментарий {}", comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(long userId, long commentId) {
        log.trace("Инициировано удаление комментария с id {} пользователем с id {}", commentId, userId);
        checkUserExistence(userId);
        Comment comment = findComment(commentId);
        checkCommentAuthorship(comment, userId);
        commentRepository.delete(comment);
        log.debug("Удален комментарий {}", comment);
    }

    @Override
    public List<CommentShortDto> getUsersComments(long userId) {
        log.trace("Инициировано получение комментариев пользователя с id {}", userId);
        checkUserExistence(userId);
        List<Comment> commentsList = commentRepository.findByAuthorId(userId);
        log.debug("Получены комментарии пользователя {}", commentsList);
        return commentsList.stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentShortDto createComment(long userId, long eventId, NewCommentDto body) {
        log.trace("Инициировано создание комментария к событию с id {} пользователем с id {}", eventId, userId);
        CommentExtra extraInfo = aggregateExtra(userId, eventId);
        Comment comment = CommentMapper.toComment(body);
        addExtra(comment, extraInfo);
        setFieldsOnCreation(comment);
        Comment saved = commentRepository.save(comment);
        log.info("Создан комментарий {}", saved);
        return CommentMapper.toCommentShortDto(saved);
    }

    private CommentExtra aggregateExtra(long userId, long eventId) {
        CommentExtra.CommentExtraBuilder extraBuilder = CommentExtra.builder();
        UserShortDto author = userService.getUser(userId);
        extraBuilder.authorId(author.getId());
        EventDto event = eventService.getEvent(eventId);
        extraBuilder.eventId(event.getId());
        return extraBuilder.build();
    }

    private void addExtra(Comment comment, CommentExtra extraInfo) {
        comment.setAuthorId(extraInfo.getAuthorId());
        comment.setEventId(extraInfo.getEventId());
    }

    private void setFieldsOnCreation(Comment comment) {
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(CommentStatus.PENDING);
    }

    @Override
    @Transactional
    public CommentShortDto updateComment(long userId, long commentId, UpdateCommentDto body) {
        log.trace("Инициировано обновление комментария с id {} пользователем с id {}", commentId, userId);
        checkUserExistence(userId);
        Comment comment = findComment(commentId);
        checkCommentAuthorship(comment, userId);
        comment.setTextOnModeration(body.getText());
        comment.setStatus(CommentStatus.PENDING);
        Comment updatedComment = commentRepository.save(comment);
        log.info("Обновлен комментарий {}", updatedComment);
        return CommentMapper.toCommentShortDto(updatedComment);
    }

    private void checkCommentAuthorship(Comment comment, long userId) {
        if (comment.getAuthorId() != userId) {
            throw new UnavailableUpdateCommentException(comment.getId());
        }
    }
}
