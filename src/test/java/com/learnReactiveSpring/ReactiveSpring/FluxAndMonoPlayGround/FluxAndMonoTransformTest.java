package com.learnReactiveSpring.ReactiveSpring.FluxAndMonoPlayGround;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("vijay", "akshay", "chandrika", "Ashwin");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase()) //VIJAY, AKSHAY, CHANDRIKA, ASHWIN
                //.map(String::toUpperCase)    //replacing lambda with method reference
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("VIJAY", "AKSHAY", "CHANDRIKA", "ASHWIN")
                .verifyComplete();

    }

    @Test
    public void transformUsingMapLength() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length()) //VIJAY, AKSHAY, CHANDRIKA, ASHWIN
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMapLengthRepeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length()) //VIJAY, AKSHAY, CHANDRIKA, ASHWIN
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMapFilter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>4)
                .map(s -> s.toUpperCase()) // ASHWIN
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ASHWIN")
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMap(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // A, B, C, D, E, F
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); // A -> List[A, newValue] , B -> List[B, newValue]
                })//db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s)  {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMapUsingParallel(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) //Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_parallel_maintain_order(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) //Flux<Flux<String> -> (A,B), (C,D), (E,F)
                /* .concatMap((s) ->
                         s.map(this::convertToList).`(parallel())) */// Flux<List<String>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
