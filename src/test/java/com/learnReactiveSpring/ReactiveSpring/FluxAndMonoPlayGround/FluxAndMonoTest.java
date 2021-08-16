package com.learnReactiveSpring.ReactiveSpring.FluxAndMonoPlayGround;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest(){
/*
.subscribe() = we need to subscribe the flux to a streams in order to emit the elements.
                or
               subscribe() pass the element from flux to subscriber.
.just() = add element to flux type
.concatWith() = append the new elements at the end of the string
.log() = prints the full flow (maintains log of every line executing)
*/

        Flux<String> stringFlux = Flux.just("Vijay", "Akshay", "Chandrika")
                //.concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("After Error"))
                .log();

        stringFlux.subscribe(System.out::println,
                        (e) -> System.out.println("Exception is : "+e),
                        () -> System.out.println("Completed"));

/*
   ------------------------log() example---------------------
17:05:43.723 [main] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
17:05:43.788 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
17:05:43.804 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
17:05:43.809 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Vijay)
Vijay
17:05:43.813 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Akshay)
Akshay
17:05:43.814 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Chandrika)
Chandrika
17:05:43.831 [main] ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception occurred)
Process finished with exit code 0
*/
    }

    @Test
    public void fluxTestElementsWithoutError(){
        Flux<String> stringFlux = Flux.just("Vijay", "Akshay", "Chandrika")
                .log();

        //stepVerifier make subscribing the element and asserting the values on the flux
        StepVerifier.create(stringFlux)
                .expectNext("Vijay")
                .expectNext("Akshay")
                .expectNext("Chandrika")
                .verifyComplete();
    }

    @Test
    public void fluxTestElementsWithError(){
        Flux<String> stringFlux = Flux.just("Vijay", "Akshay", "Chandrika")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                //.expectNextCount(3)
                .expectNext("Vijay", "Akshay", "Chandrika")
                .expectErrorMessage("Error Available")
                .verify();
    }

    @Test
    public void monoTest(){
        Mono<String> stringMono = Mono.just("Vijay");

        StepVerifier.create(stringMono.log())
                //.expectNextCount(1)
                .expectNext("Vijay")
                .verifyComplete();
    }

    @Test
    public void monoTestWithError(){
        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                //.expectNextCount(1)
                .expectError(RuntimeException.class)
                .verify();
    }
}


