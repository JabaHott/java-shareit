package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.page.PageRequestHandler;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        checkUserExistence(userId);

        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userRepository.getReferenceById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        checkUserExistence(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(Integer from, Integer size, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequestHandler.getPageRequest(from, size, sort);

        return requestRepository.findAll().stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        checkUserExistence(userId);
        checkItemRequestExistence(requestId);

        return requestMapper.toItemRequestDto(requestRepository.getReferenceById(requestId));
    }

    private void checkUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            String errorMessage = String.format("Пользователь c id = %d не найден!", userId);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    private void checkItemRequestExistence(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            String errorMessage = String.format("Запрос c id = %d не найден!", requestId);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}