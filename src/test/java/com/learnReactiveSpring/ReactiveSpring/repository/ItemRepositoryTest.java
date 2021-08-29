package com.learnReactiveSpring.ReactiveSpring.repository;

import com.learnReactiveSpring.ReactiveSpring.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "SAMSUNG TV", 35000.00),
            new Item(null, "APPLE TV", 60000.00),
            new Item("idOnida", "Onida Tv", 25000.00),
            new Item(null, "SONY TV", 40000.00),
            new Item(null, "LG TV", 30000.00));

    @Before  //this method executes before all testCases
    public void createItems(){
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Item is : " +item))
                .blockLast();   //blocks until all items are saved
    }

    @Test
    public void getAllItems(){
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemsById(){
        StepVerifier.create(itemRepository.findById("idOnida"))
                .expectSubscription()
                .expectNextMatches(item ->  item.getDescription().equalsIgnoreCase("Onida Tv"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription(){
        StepVerifier.create(itemRepository.findByDescriptionIgnoreCase("sony tv").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        StepVerifier.create(itemRepository.save(new Item(null , "MI Tv", 22000.00)).log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() > 20000)
                .verifyComplete();
    }

    @Test
    public void saveItemMono(){
        Item itemObj = new Item(null , "MI Tv", 22000.00);
        Mono<Item> savedItem = itemRepository.save(itemObj);

        StepVerifier.create(savedItem.log("saved item is : "))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() > 20000 && item.getDescription().equalsIgnoreCase("mi tv"))
                .verifyComplete();
    }

    @Test
    public void updateItem(){
        Mono<Item> itemFlux = itemRepository.findByDescriptionIgnoreCase("sony tv")
                        .map(item -> { item.setPrice(45000.00);
                            return item;
                        }).flatMap(itemRepository::save);

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 45000.00)
                .verifyComplete();
    }

    @Test
    public void deleteItem(){
        Mono<Void> itemFlux = itemRepository.deleteById("idOnida");

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

        //verify the item deleted or not
        StepVerifier.create(itemRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }
}
