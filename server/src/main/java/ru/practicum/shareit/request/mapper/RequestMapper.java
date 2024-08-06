package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

}