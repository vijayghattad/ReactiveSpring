package com.learnReactiveSpring.ReactiveSpring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getFluxOfInteger() {

        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> getFluxOfIntegerStream() {

        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxinfinitestream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getFluxOfInfiniteStream() {
        return Flux.interval(Duration.ofMillis(1000))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> getMono() {
        return Mono.just(1).log();
    }

}
