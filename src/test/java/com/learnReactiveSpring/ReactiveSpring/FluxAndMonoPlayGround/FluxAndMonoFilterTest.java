package com.learnReactiveSpring.ReactiveSpring.FluxAndMonoPlayGround;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("vijay","akshay","chandrika","Ashwin");

    @Test
    public void filterTest(){

        Flux<String> namesFlux = Flux.fromIterable(names) // vijay, akshay, chandrika,Ashwin
                .filter(s->s.startsWith("a"))
                .log(); //vijay,akshay

        StepVerifier.create(namesFlux)
                .expectNext("vijay","akshay")
                .verifyComplete();

    }

    @Test
    public void filterTestLength(){

        Flux<String> namesFlux = Flux.fromIterable(names) // vijay, akshay, chandrika,Ashwin
                .filter(s->s.length() >4)
                .log(); //Ashwin

        StepVerifier.create(namesFlux)
                .expectNext("Ashwin")
                .verifyComplete();

    }

}
