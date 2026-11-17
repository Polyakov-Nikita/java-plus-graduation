package ru.practicum.explorewithme.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.common.dto.RequestStatus;

import java.util.List;

@Builder
@Data
public class UpdateRequestStatusDto {
    List<Long> requestIds;
    @NotNull
    RequestStatus status;
}
