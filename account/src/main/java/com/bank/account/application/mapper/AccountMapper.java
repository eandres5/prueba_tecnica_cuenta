package com.bank.account.application.mapper;

import com.bank.account.domain.model.Account;
import com.bank.account.infrastructure.adapter.web.dto.AccountRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountResponseDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountUpdateDTO;
import org.mapstruct.*;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {

    /**
     * Converts AccountRequestDTO to Account entity
     */
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "currentBalance", source = "initialBalance")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Account toEntity(AccountRequestDTO dto);

    /**
     * Converts Account entity to AccountResponseDTO
     */
    AccountResponseDTO toResponseDTO(Account account);

    /**
     * Updates Account entity from AccountUpdateDTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateFromDto(AccountUpdateDTO dto, @MappingTarget Account account);
}