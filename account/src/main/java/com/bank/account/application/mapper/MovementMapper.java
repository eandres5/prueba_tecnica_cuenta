package com.bank.account.application.mapper;

import com.bank.account.domain.model.Movement;
import com.bank.account.infrastructure.adapter.web.dto.MovementRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.MovementResponseDTO;
import com.bank.account.infrastructure.adapter.web.dto.MovementDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between Movement entities and DTOs
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovementMapper {

    /**
     * Converts MovementRequestDTO to Movement entity
     */
    @Mapping(target = "movementId", ignore = true)
    @Mapping(target = "movementDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Movement toEntity(MovementRequestDTO dto);

    /**
     * Converts Movement entity to MovementResponseDTO
     */
    @Mapping(target = "accountNumber", ignore = true)
    MovementResponseDTO toResponseDTO(Movement movement);

    /**
     * Converts Movement entity to MovementDetailDTO (for reports)
     */
    MovementDetailDTO toDetailDTO(Movement movement);
}