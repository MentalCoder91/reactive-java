package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoGeneratorServiceTest {


    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                // .expectNext("ben", "alex", "chloe")
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map();

        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();

    }


    @Test
    void namesMono() {
        var namesMono = fluxAndMonoGeneratorService.namesMono();
        StepVerifier.create(namesMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_filter() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_filter(3);
        StepVerifier.create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_map_doOnNext() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map_doOnNext(3);
        StepVerifier.create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap(3);
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_async() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap_async(3);
        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(3);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {

        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatMap(3);
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "N", "I", "S", "H"))
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {

        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatMapMany(3);
        StepVerifier.create(namesFlux)
                .expectNext("A", "N", "I", "S", "H")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(3);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_defaultIfEmpty() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_defaultEmpty(6);

        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {


        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(3);

        //>6
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();

        //<=3
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void namesMono_map_filter_defaultIfEmpty() {

        var namesMono = fluxAndMonoGeneratorService.namesMono_map_filter_defaultIfEmpty(3);
        StepVerifier.create(namesMono)
                .expectNext("ANISH")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_switchIfEmpty() {

        var namesMono = fluxAndMonoGeneratorService.namesMono_map_filter_switchIfEmpty(10);
        StepVerifier.create(namesMono)
                .expectNext("DEFAULT")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concat() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_concat(6);

        StepVerifier.create(stringFlux)
                .expectNext("alex", "ben", "chloe", "anish", "piyaaa", "ruchita")
                .verifyComplete();
    }

    @Test
    void namesMono_concatWith() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesMono_concatWith();

        StepVerifier.create(stringFlux)
                .expectNext("ANISH", "PIYAA")
                .verifyComplete();
    }

    @Test
    void namesFlux_merge() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_merge();

        StepVerifier.create(stringFlux)
                .expectNext("a", "d", "b", "e", "c", "f")
                .verifyComplete();
    }

    @Test
    void namesMono_mergeWith() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesMono_mergeWith();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void namesFlux_mergeSequential() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_mergeSequential();

        StepVerifier.create(stringFlux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void namesFlux_zip() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_zip();

        StepVerifier.create(stringFlux)
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }

    @Test
    void namesFlux_zip4() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_zip4();

        StepVerifier.create(stringFlux)
                .expectNext("ad14", "be25", "cf36")
                .verifyComplete();
    }

    @Test
    void namesFlux_zipWith() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_zipWith();

        StepVerifier.create(stringFlux)
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }

    @Test
    void namesMono_zipWith() {
        Mono<String> stringFlux = fluxAndMonoGeneratorService.namesMono_zipWith();

        StepVerifier.create(stringFlux)
                .expectNext("A->B")
                .verifyComplete();
    }

    @Test
    void exception_flux() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();


    }

    @Test
    void exception_flux_1() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();


    }

    @Test
    void exception_flux_2() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C")
                .expectErrorMessage("There is an error")
                .verify();


    }

    @Test
    void exception_onErrorReturn() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorReturn();
        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void exception_onErrorResume() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorResume(new IllegalStateException("There is an error"));
        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C", "D","E")
                .verifyComplete();
    }

    @Test
    void exception_onErrorResume_1() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorResume(new RuntimeException("There is an error"));
        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void exception_onErrorContinue() {

        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorContinue();
        StepVerifier.create(stringFlux)
                .expectNext("A", "C","D")
                .verifyComplete();


    }

    @Test
    void exception_onErrorMap() {


        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorMap();
        StepVerifier.create(stringFlux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exception_doOnError() {


        Flux<String> stringFlux = fluxAndMonoGeneratorService.exception_doOnError();
        StepVerifier.create(stringFlux)
                .expectNext("A")
                .expectError()
                .verify();
    }

    @Test
    void exception_onErrorReturn_Mono() {

        Mono<Object> stringFlux = fluxAndMonoGeneratorService.exception_onErrorReturn_Mono();
        StepVerifier.create(stringFlux)
                .expectNext("abc")
                .verifyComplete();


    }

    @Test
    void exception_onErrorResume_Mono() {

        Mono<Object> stringFlux = fluxAndMonoGeneratorService.exception_onErrorResume_Mono(new IllegalStateException("There is an error"));
        StepVerifier.create(stringFlux)
                .expectNext("D")
                .verifyComplete();
    }

    @Test
    void exception_onErrorContinue_Mono() {

        Mono<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorContinue_Mono("A");
        StepVerifier.create(stringFlux)
                .expectNext("A")
                .verifyComplete();
    }


    @Test
    void exception_onErrorContinue_Mono_1() {

        Mono<String> stringFlux = fluxAndMonoGeneratorService.exception_onErrorContinue_Mono("abc");
        StepVerifier.create(stringFlux)
                .verifyComplete();
    }
}


