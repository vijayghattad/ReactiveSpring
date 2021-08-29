package com.learnReactiveSpring.ReactiveSpring.controller;

import com.learnReactiveSpring.ReactiveSpring.model.Item;
import com.learnReactiveSpring.ReactiveSpring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("api/v1/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    //create item
    @PostMapping("")
    public Mono<ResponseEntity<Item>> createItem(@RequestBody Item item){
        return itemRepository.save(item)
                .map(item1 -> new ResponseEntity<>(item1, HttpStatus.CREATED))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    //get item by id
    @GetMapping("{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id) {
        return itemRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //get all items
    @GetMapping("")
    public Flux<Item> getAllItems(){
        return itemRepository.findAll();
    }

    //delete item by id
    @DeleteMapping("{id}")
    public Mono<Void> deleteById(@PathVariable String id){
        return itemRepository.deleteById(id);
    }

    //update item by id
    @PutMapping("{id}")
    public Mono<ResponseEntity<Item>> updateItemById(@PathVariable String id, @RequestBody Item input){
        return getItemById(id)
                .flatMap(itemResponseEntity -> {
                    itemResponseEntity.getBody().setPrice(input.getPrice());
                    itemResponseEntity.getBody().setDescription(input.getDescription());
                    return itemRepository.save(itemResponseEntity.getBody());
                })
                .map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND ))
                .onErrorReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
