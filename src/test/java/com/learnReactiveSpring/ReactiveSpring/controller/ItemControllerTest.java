package com.learnReactiveSpring.ReactiveSpring.controller;

import com.learnReactiveSpring.ReactiveSpring.model.Item;
import com.learnReactiveSpring.ReactiveSpring.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    public List<Item> data() {

        return Arrays.asList(new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 149.99));
    }


    @Before
    public void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is : " + item);
                }))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri("api/v1/item")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri("api/v1/item")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith((response) -> {
                    List<Item> items = response.getResponseBody();
                    if (items != null && items.size() > 0) {
                        items.forEach((item) -> {
                            assertNotNull(item.getItemId());
                        });
                    }
                });
    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemsFlux = webTestClient.get().uri("api/v1/item")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("value from network : "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem(){

        webTestClient.get().uri("api/v1/item/{id}","ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 149.99);

    }

    @Test
    public void getOneItem_notFound(){

        webTestClient.get().uri("api/v1/item/{id}","DEF")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItem(){

        Item item = new Item(null, "Iphone X", 999.99);

        webTestClient.post().uri("api/v1/item")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(999.99);



    }

    @Test
    public void deleteItem(){

        webTestClient.delete().uri("api/v1/item/{id}","ABC")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

    }

    @Test
    public void updateItem(){
        double newPrice =129.99;
        Item item = new Item(null,"Beats HeadPhones", newPrice);

        webTestClient.put().uri("api/v1/item/{id}","ABC")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",newPrice);

    }

    @Test
    public void updateItem_notFound(){
        double newPrice =129.99;
        Item item = new Item(null,"Beats HeadPhones", newPrice);

        webTestClient.put().uri("api/v1/item/{id}","DEF")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }
}
