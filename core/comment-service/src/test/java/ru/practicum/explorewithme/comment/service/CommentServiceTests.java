package ru.practicum.explorewithme.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.explorewithme.comment.dto.*;
import ru.practicum.explorewithme.comment.entity.Comment;
import ru.practicum.explorewithme.comment.exception.CommentNotFoundException;
import ru.practicum.explorewithme.comment.exception.UnavailableUpdateCommentException;
import ru.practicum.explorewithme.comment.exception.UserNotFoundException;
import ru.practicum.explorewithme.comment.repository.CommentRepository;
import ru.practicum.explorewithme.comment.repository.specification.AdminCommentSearchSpecification;
import ru.practicum.explorewithme.common.dto.EventDto;
import ru.practicum.explorewithme.common.dto.UserShortDto;
import ru.practicum.explorewithme.common.pagination.OffsetPageRequest;
import ru.practicum.explorewithme.common.service.EventService;
import ru.practicum.explorewithme.common.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    private static final long ADMIN_ID = 1L;
    private static final long COMMENT_ID = 2L;
    private static final long EVENT_ID = 3L;
    private static final long AUTHOR_ID = 4L;
    private static final long ANOTHER_USER_ID = 99L;

    private UserShortDto admin;
    private UserShortDto author;
    private UserShortDto anotherUser;
    private Comment comment;

    @BeforeEach
    void setUp() {
        admin = UserShortDto.builder().id(ADMIN_ID).build();
        author = UserShortDto.builder().id(AUTHOR_ID).build();
        anotherUser = UserShortDto.builder().id(ANOTHER_USER_ID).build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .text("Старый текст")
                .textOnModeration("Новый текст на модерации")
                .created(LocalDateTime.now())
                .eventId(EVENT_ID)
                .authorId(author.getId())
                .status(CommentStatus.PENDING)
                .build();
    }

    @Test
    void searchComments_returnsEmptyList_whenNoCommentsFound() {
        Page<Comment> emptyPage = new PageImpl<>(List.of());
        when(commentRepository.findAll(any(AdminCommentSearchSpecification.class), any(OffsetPageRequest.class)))
                .thenReturn(emptyPage);

        List<CommentDto> result = commentService.searchComments(ADMIN_ID, null, null, null, null, null, null, 0, 10);

        assertThat(result).isEmpty();
        verify(commentRepository).findAll(any(AdminCommentSearchSpecification.class), any(OffsetPageRequest.class));
    }

    @Test
    void searchComments_returnsMappedDtos_whenCommentsFound() {
        EventDto event1 = EventDto.builder()
                .id(1L)
                .build();

        EventDto event2 = EventDto.builder()
                .id(2L)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Текст 1")
                .eventId(event1.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Текст 2")
                .eventId(event2.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .build();
        Page<Comment> page = new PageImpl<>(List.of(comment1, comment2));

        when(commentRepository.findAll(any(AdminCommentSearchSpecification.class), any(OffsetPageRequest.class))).thenReturn(page);

        List<CommentDto> result = commentService.searchComments(ADMIN_ID, "поиск", List.of(AUTHOR_ID), "2024-01-01 10:00:00", "2024-12-31 10:00:00", List.of(EVENT_ID), null, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("Текст 1");
        assertThat(result.get(1).getText()).isEqualTo("Текст 2");
        verify(commentRepository, times(1)).findAll(any(AdminCommentSearchSpecification.class), any(OffsetPageRequest.class));
    }

    @Test
    void moderateComment_approveAction_acceptsPendingTextAndClearsDraft() {
        EventDto event = EventDto.builder()
                .id(1L)
                .build();

        String pendingText = "Текст после правки";
        comment = Comment.builder()
                .id(1L)
                .text("Старый текст")
                .eventId(event.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .textOnModeration(pendingText)
                .status(CommentStatus.PENDING) // или любой другой, не важно
                .build();

        when(userService.existsById(ADMIN_ID)).thenReturn(true);
        when(userService.getUser(eq(ADMIN_ID))).thenReturn(admin);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        when(commentRepository.save(eq(comment))).thenReturn(comment);

        CommentDto dto = commentService.moderateComment(ADMIN_ID, COMMENT_ID, ModerationAction.APPROVE);

        verify(commentRepository).save(comment);
        assertThat(dto.getText()).isEqualTo(pendingText);
        assertThat(comment.getTextOnModeration()).isNull();
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.APPROVED);
        assertThat(comment.getModeratorId()).isEqualTo(admin.getId());
        assertThat(comment.getModerated()).isNotNull();
    }

    @Test
    void moderateComment_rejectAction_keepsOriginalTextAndClearsDraft() {
        EventDto event = EventDto.builder()
                .id(1L)
                .build();

        String originalText = "Старый текст";
        String pendingText = "Неудачная правка";
        comment = Comment.builder()
                .id(1L)
                .text(originalText)
                .eventId(event.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .moderated(LocalDateTime.now())
                .textOnModeration(pendingText)
                .status(CommentStatus.PENDING)
                .build();

        when(userService.existsById(ADMIN_ID)).thenReturn(true);
        when(userService.getUser(eq(ADMIN_ID))).thenReturn(admin);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        when(commentRepository.save(eq(comment))).thenReturn(comment);

        CommentDto dto = commentService.moderateComment(ADMIN_ID, COMMENT_ID, ModerationAction.REJECT);

        verify(commentRepository).save(comment);
        assertThat(dto.getText()).isEqualTo(originalText);
        assertThat(comment.getTextOnModeration()).isNull();
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.APPROVED);
        assertThat(comment.getModeratorId()).isEqualTo(admin.getId());
        assertThat(comment.getModerated()).isNotNull();
    }

    @Test
    void moderateComment_throwsNotFound_whenCommentDoesNotExist() {
        when(userService.existsById(ADMIN_ID)).thenReturn(true);
        when(userService.getUser(eq(ADMIN_ID))).thenReturn(admin);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.moderateComment(ADMIN_ID, COMMENT_ID, ModerationAction.APPROVE))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void moderateComment_throwsNotFound_whenAdminDoesNotExist() {
        when(userService.existsById(ADMIN_ID)).thenReturn(false);

        assertThatThrownBy(() -> commentService.moderateComment(ADMIN_ID, COMMENT_ID, ModerationAction.APPROVE))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteComment_deletesComment_ByAdmin_whenExists() {
        when(userService.existsById(ADMIN_ID)).thenReturn(true);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));

        commentService.deleteCommentByAdmin(ADMIN_ID, COMMENT_ID);

        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("getComments должен вернуть список комментариев события")
    void getComments_shouldReturnComments() {
        long eventId = 1L;
        int from = 0;
        int size = 10;

        Comment firstComment = makeComment(1L, "Первый комментарий", eventId);
        Comment secondComment = makeComment(2L, "Второй комментарий", eventId);

        when(commentRepository.findByEventIdWithOffset(eventId, from, size))
                .thenReturn(List.of(firstComment, secondComment));

        List<CommentShortDto> result = commentService.getComments(eventId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Первый комментарий", result.get(0).getText());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Второй комментарий", result.get(1).getText());

        verify(commentRepository).findByEventIdWithOffset(eventId, from, size);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    @DisplayName("getComments должен вернуть пустой список, если комментариев нет")
    void getComments_whenCommentsNotFound_shouldReturnEmptyList() {
        long eventId = 1L;
        int from = 0;
        int size = 10;

        when(commentRepository.findByEventIdWithOffset(eventId, from, size))
                .thenReturn(List.of());

        List<CommentShortDto> result = commentService.getComments(eventId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository).findByEventIdWithOffset(eventId, from, size);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    @DisplayName("getComments должен передать в репозиторий корректные параметры пагинации")
    void getComments_shouldPassCorrectPaginationParamsToRepository() {
        long eventId = 5L;
        int from = 20;
        int size = 50;

        when(commentRepository.findByEventIdWithOffset(eventId, from, size))
                .thenReturn(List.of());

        commentService.getComments(eventId, from, size);

        verify(commentRepository).findByEventIdWithOffset(eventId, from, size);
        verifyNoMoreInteractions(commentRepository);
    }

    private Comment makeComment(Long id, String text, Long eventId) {
        EventDto event = makeEvent(eventId);
        UserShortDto author = makeUser(100L, "Author");
        UserShortDto moderator = makeUser(200L, "Moderator");

        return Comment.builder()
                .id(id)
                .text(text)
                .created(LocalDateTime.of(2024, 1, 1, 12, 0))
                .updated(LocalDateTime.of(2024, 1, 1, 13, 0))
                .eventId(event.getId())
                .authorId(author.getId())
                .status(CommentStatus.PENDING)
                .moderatorId(moderator.getId())
                .moderated(LocalDateTime.of(2024, 1, 1, 14, 0))
                .build();
    }

    private EventDto makeEvent(Long id) {
        return EventDto.builder()
                .id(id)
                .build();
    }

    private UserShortDto makeUser(Long id, String name) {
        return UserShortDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    void getUsersComments_returnsList_whenUserExists() {
        EventDto event1 = EventDto.builder()
                .id(1L)
                .build();

        EventDto event2 = EventDto.builder()
                .id(2L)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Текст 1")
                .eventId(event1.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Текст 2")
                .eventId(event2.getId())
                .authorId(author.getId())
                .moderatorId(admin.getId())
                .build();

        List<Comment> comments = List.of(comment1, comment2);
        when(userService.existsById(AUTHOR_ID)).thenReturn(true);
        when(commentRepository.findByAuthorId(eq(AUTHOR_ID))).thenReturn(comments);

        List<CommentShortDto> result = commentService.getUsersComments(AUTHOR_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("Текст 1");
        assertThat(result.get(1).getText()).isEqualTo("Текст 2");
        verify(commentRepository).findByAuthorId(AUTHOR_ID);
    }

    @Test
    void getUsersComments_returnsEmptyList_whenNoComments() {
        when(userService.existsById(AUTHOR_ID)).thenReturn(true);
        when(commentRepository.findByAuthorId(eq(AUTHOR_ID))).thenReturn(List.of());

        List<CommentShortDto> result = commentService.getUsersComments(AUTHOR_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void createComment_createsNewComment_successfully() {
        EventDto event = EventDto.builder()
                .id(1L)
                .build();

        NewCommentDto dto = NewCommentDto.builder().text("Новый комментарий").build();
        when(userService.getUser(eq(AUTHOR_ID))).thenReturn(author);
        when(eventService.getEvent(eq(EVENT_ID))).thenReturn(event);

        Comment savedComment = Comment.builder()
                .id(1L)
                .eventId(event.getId())
                .authorId(author.getId())
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentShortDto result = commentService.createComment(AUTHOR_ID, EVENT_ID, dto);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment captured = captor.getValue();
        assertThat(captured.getAuthorId()).isEqualTo(author.getId());
        assertThat(captured.getEventId()).isEqualTo(event.getId());
        assertThat(captured.getTextOnModeration()).isEqualTo("Новый комментарий");
        assertThat(captured.getStatus()).isEqualTo(CommentStatus.PENDING);
        assertThat(captured.getCreated()).isNotNull();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo(null);
    }

    @Test
    void updateComment_updatesTextToModeration_andSetsPending_whenOwnComment() {
        UpdateCommentDto dto = UpdateCommentDto.builder().text("Обновленный текст").build();
        when(userService.existsById(AUTHOR_ID)).thenReturn(true);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));

        Comment savedComment = comment;
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentShortDto result = commentService.updateComment(AUTHOR_ID, COMMENT_ID, dto);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment captured = captor.getValue();
        assertThat(captured.getText()).isEqualTo("Старый текст");
        assertThat(captured.getTextOnModeration()).isEqualTo("Обновленный текст");
        assertThat(captured.getStatus()).isEqualTo(CommentStatus.PENDING);

        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getText()).isEqualTo("Старый текст");
    }

    @Test
    void updateComment_throwsUnavailableUpdate_whenUserTriesToUpdateOthersComment() {
        UpdateCommentDto dto = UpdateCommentDto.builder().text("Текст").build();

        when(userService.existsById(ANOTHER_USER_ID)).thenReturn(true);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(ANOTHER_USER_ID, COMMENT_ID, dto))
                .isInstanceOf(UnavailableUpdateCommentException.class);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_throwsNotFound_whenCommentDoesNotExist() {
        UpdateCommentDto dto = UpdateCommentDto.builder().text("Текст").build();
        when(userService.existsById(AUTHOR_ID)).thenReturn(true);
        when(commentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment(AUTHOR_ID, COMMENT_ID, dto))
                .isInstanceOf(CommentNotFoundException.class);

        verify(commentRepository, never()).save(any());
    }
}
