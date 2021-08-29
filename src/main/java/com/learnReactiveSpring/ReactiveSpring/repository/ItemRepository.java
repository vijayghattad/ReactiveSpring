package com.learnReactiveSpring.ReactiveSpring.repository;

import com.learnReactiveSpring.ReactiveSpring.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    Mono<Item> findByDescriptionIgnoreCase(String description);
}
