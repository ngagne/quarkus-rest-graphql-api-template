package com.example.api.mapper;

import com.example.api.model.CreateCustomerProfileInput;
import com.example.api.model.CreateCustomerProfileRequest;
import com.example.api.model.CustomerCoreProfile;
import com.example.api.model.UpdateCustomerProfileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting between REST/GraphQL DTOs and domain models.
 * Uses MapStruct for compile-time code generation to avoid manual mapping.
 */
@Mapper
public interface CustomerProfileMapper {

    CustomerProfileMapper INSTANCE = Mappers.getMapper(CustomerProfileMapper.class);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "givenName", source = "givenName")
    @Mapping(target = "familyName", source = "familyName")
    @Mapping(target = "segment", source = "segment")
    @Mapping(target = "baseCurrency", source = "baseCurrency")
    @Mapping(target = "availableBalance", source = "availableBalance")
    CustomerCoreProfile toCustomerCoreProfile(CreateCustomerProfileInput input);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "givenName", source = "givenName")
    @Mapping(target = "familyName", source = "familyName")
    @Mapping(target = "segment", source = "segment")
    @Mapping(target = "baseCurrency", source = "baseCurrency")
    @Mapping(target = "availableBalance", source = "availableBalance")
    CustomerCoreProfile toCustomerCoreProfile(CreateCustomerProfileRequest request);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "givenName", source = "givenName")
    @Mapping(target = "familyName", source = "familyName")
    @Mapping(target = "segment", source = "segment")
    @Mapping(target = "baseCurrency", source = "baseCurrency")
    @Mapping(target = "availableBalance", source = "availableBalance")
    CustomerCoreProfile toCustomerCoreProfile(UpdateCustomerProfileRequest request);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "givenName", source = "givenName")
    @Mapping(target = "familyName", source = "familyName")
    @Mapping(target = "segment", source = "segment")
    @Mapping(target = "baseCurrency", source = "baseCurrency")
    @Mapping(target = "availableBalance", source = "availableBalance")
    CreateCustomerProfileInput toCreateCustomerProfileInput(CreateCustomerProfileRequest request);
}
