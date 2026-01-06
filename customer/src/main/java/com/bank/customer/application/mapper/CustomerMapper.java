package com.bank.customer.application.mapper;

import com.bank.customer.domain.model.Customer;
import com.bank.customer.domain.model.Person;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerRequestDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerResponseDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = LocalDateTime.class
)
public interface CustomerMapper {

    /* ===================== CREATE ===================== */

    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Person toPersonEntity(CustomerRequestDTO dto);

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "personId", source = "personId")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Customer toCustomerEntity(CustomerRequestDTO dto, Long personId);

    /* ===================== RESPONSE ===================== */

    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "name", source = "person.name")
    @Mapping(target = "gender", source = "person.gender")
    @Mapping(target = "identification", source = "person.identification")
    @Mapping(target = "address", source = "person.address")
    @Mapping(target = "phone", source = "person.phone")
    @Mapping(target = "status", source = "customer.status")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CustomerResponseDTO toResponseDTO(Person person, Customer customer);

    /* ===================== UPDATE ===================== */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "identification", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    void updatePersonFromDto(CustomerUpdateDTO dto, @MappingTarget Person person);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    void updateCustomerFromDto(CustomerUpdateDTO dto, @MappingTarget Customer customer);
}
