package com.learnReactiveSpring.ReactiveSpring.FluxAndMonoPlayGround;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("vijay","akshay","chandrika","Ashwin");

    @Test
    public void fluxUsingIterable(){

        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("vijay","akshay","chandrika","Ashwin")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){

        String[] names = new String[]{"vijay","akshay","chandrika","Ashwin"};

        Flux<String> namesFlux = Flux.fromArray(names);
        StepVerifier.create(namesFlux)
                .expectNext("vijay","akshay","chandrika","Ashwin")
                .verifyComplete();

    }

    @Test
    public void fluxUsingStream(){

        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("vijay","akshay","chandrika","Ashwin")
                .verifyComplete();

    }

    @Test
    public void monoUsingJustOrEmpty(){

        Mono<String> mono = Mono.justOrEmpty(null); //Mono.Empty();

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){

        //supplier will not store the values it only returns

        Supplier<String> stringSupplier = () -> "vijay";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        System.out.println(stringSupplier.get());

        StepVerifier.create(stringMono.log())
                .expectNext("vijay")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){

       Flux<Integer> integerFlux = Flux.range(1,5).log();

       StepVerifier.create(integerFlux)
               .expectNext(1,2,3,4,5)
               .verifyComplete();
    }
}
