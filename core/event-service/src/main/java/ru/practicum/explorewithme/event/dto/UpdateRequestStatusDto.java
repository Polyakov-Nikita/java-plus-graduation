package ru.practicum.explorewithme.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.explorewithme.common.dto.RequestStatus;

import java.util.List;

@Builder
@Getter
@ToString
public class UpdateRequestStatusDto {
    List<Long> requestIds;
    @NotNull
    RequestStatus status;
}
