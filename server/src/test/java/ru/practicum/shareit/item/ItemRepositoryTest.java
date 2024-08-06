package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        User owner = new User(1L, "name", "loh@mail.ru");
        item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Description for item 1");
        item1.setAvailable(true);
        item1.setOwner(owner);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Description for item 2");
        item2.setAvailable(false);
        item2.setOwner(owner);

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    @Rollback
    public void testSearchUnavailableItems() {
        List<Item> items = itemRepository.search("Description for item 2");

        assertThat(items).isEmpty();
    }
}