package com.learnReactiveSpring.ReactiveSpring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)   //used to configure a unit test that required Spring's dependency injection.  We can run JUnit5 tests with any other older JUnit environment using the @RunWith annotation.
@WebFluxTest   //It scans all the classes annotated with @RestController and @Controller only. But it does not scan @Component, @Service, @Repository
public class FluxAndMonoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void fluxApproach1(){
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)     //Not necessary to mention (Optional -> took default type)
                .exchange()                                  //Creates actual call to endPoint
                .expectStatus().isOk()                       //Checks expected Status
                .returnResult(Integer.class)                 //Checks return result type
                .getResponseBody();                          //Returns response

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void fluxApproach2(){
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)      //Returns mentioned MediaType
                .expectBodyList(Integer.class)                                    //Returns Integer List as response
                .hasSize(4);                                                      //hasSize checks size of response
    }

    @Test
    public void fluxApproach3(){
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(integerList, entityExchangeResult.getResponseBody());
    }

    @Test
    public void fluxApproach4(){
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedIntegerList, response.getResponseBody());
                });

    }

    @Test
    public void fluxApproachForInfiniteStream(){
        Flux<Long> longFlux = webTestClient.get().uri("/fluxinfinitestream")
                .accept(MediaType.APPLICATION_STREAM_JSON)    //Not necessary to mention (Optional -> took default type)
                .exchange()                                   //Creates actual call to endPoint
                .expectStatus().isOk()                       //Checks expected Status
                .returnResult(Long.class)                    //Checks return result type
                .getResponseBody();                          //Returns response

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L, 1l, 2l, 3l)
                .thenCancel()
                .verify();
    }

    @Test
    public void MonoTest(){
        Integer expectedValue = 1;

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedValue, response.getResponseBody());
                });
    }
}
