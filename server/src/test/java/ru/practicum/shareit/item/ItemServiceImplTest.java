package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void shouldExceptionWhenEditNotExistingItem() {
        ItemService itemService = new ItemServiceImpl(mockItemRepository, mockUserRepository, null,
                null, null, null, null);

        when(mockUserRepository.existsById(any(Long.class)))
                .thenReturn(true);

        when(mockItemRepository.existsById(any(Long.class)))
                .thenReturn(false);

        ItemDto itemDto = new ItemDto(1L, "item1", "description1", true, null);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, itemDto.getId(), 2L));
        assertEquals(String.format("Указанный товар %d не найден!", itemDto.getId()), exception.getMessage());
    }
}