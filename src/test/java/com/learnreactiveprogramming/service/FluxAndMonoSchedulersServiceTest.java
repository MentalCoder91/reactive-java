package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {



    FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();
    @Test
    void explore_publishOn() {

        Flux<String> stringFlux = service.explore_publishOn();

        StepVerifier.create(stringFlux)
                .expectNextCount(8)
                .verifyComplete();

    }

    @Test
    void explore_subscribeOn() {


        Flux<String> stringFlux = service.explore_subscribeOn();

        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel() {

        ParallelFlux<String> stringFlux = service.explore_parallel();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_usingFlatMap() {

        Flux<String> stringFlux = service.explore_parallel_usingFlatMap();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_1() {


        Flux<String> stringFlux = service.explore_parallel_1();

        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel_usingFlatMapSequential() {


        Flux<String> stringFlux = service.explore_parallel_usingFlatMapSequential();

        StepVerifier.create(stringFlux)
                .expectNext("ALEX","BEN","CHLOE","ANU")
                .verifyComplete();
    }
}