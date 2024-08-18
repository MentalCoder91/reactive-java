package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class FluxAndMonoGeneratorService {


    Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log(); // db or remote service call.
    }


    Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log(); // db or remote service call.
    }


    Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    Flux<String> namesFlux_filter(int strLen) {//length greater than length
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(x -> x.length() > strLen)
                .map(x -> x.length() + "-" + x)
                .log(); // db or remote service call.
    }

    Flux<String> namesFlux_map_doOnNext(int strLen) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(x -> x.length() > strLen)
                .map(x -> x.length() + "-" + x)
                .doOnNext(name -> {
                    System.out.println("name:" + name);
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription: " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("The task is complete on callBack");
                })
                .doFinally(signal -> {
                    System.out.println("inside doFinally:" + signal.name());
                })
                .log(); // db or remote service call.
    }


    Flux<String> namesFlux_flatMap(int strLen) {//length greater than length
        //All by main thread
        /*
        12:24:08.183 [main] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
        12:24:08.213 [main] INFO reactor.Flux.FlatMap.1 - onSubscribe(FluxFlatMap.FlatMapMain)
            12:24:08.216 [main] INFO reactor.Flux.FlatMap.1 - request(unbounded)
            12:24:08.218 [main] INFO reactor.Flux.FlatMap.1 - onNext(A)
            12:24:08.218 [main] INFO reactor.Flux.FlatMap.1 - onNext(L)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(E)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(X)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(C)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(H)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(L)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(O)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onNext(E)
            12:24:08.219 [main] INFO reactor.Flux.FlatMap.1 - onComplete()

         */
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(x -> x.length() > strLen)
                .flatMap(splitToFlux)
                .log(); // db or remote service call.
    }

    Flux<String> namesFlux_flatMap_async(int strLen) {//length greater than length
        //flatMap does not guarantee ordering i.e either ALEX will come first or CHLOE // Execute on different thread
        //because of delay provided // No of threads-> No of cores
        /*

                    [parallel-4] INFO reactor.Flux.FlatMap.1 - onNext(H)
            12:20:12.651 [parallel-4] INFO reactor.Flux.FlatMap.1 - onNext(L)
            12:20:13.661 [parallel-6] INFO reactor.Flux.FlatMap.1 - onNext(L)
            12:20:13.661 [parallel-6] INFO reactor.Flux.FlatMap.1 - onNext(E)
            12:20:14.675 [parallel-8] INFO reactor.Flux.FlatMap.1 - onNext(O)
            12:20:14.675 [parallel-8] INFO reactor.Flux.FlatMap.1 - onNext(X)
            12:20:15.687 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(E)
            12:20:15.687 [parallel-1] INFO reactor.Flux.FlatMap.1 - onComplete()


         */
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(x -> x.length() > strLen)
                .flatMap(splitToFluxWithDelay)
                .log(); // db or remote service call.
    }


    Flux<String> namesFlux_concatMap(int strLen) {//length greater than length
        //flatMap does not guarantee ordering i.e either ALEX will come first or CHLOE
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(x -> x.length() > strLen)
                .concatMap(splitToFluxWithDelay)
                .log(); // db or remote service call.
    }

    Flux<String> namesFlux_transform(int strLen) {//length greater than length

        Function<Flux<String>, Flux<String>> fluxFn = (nameFlux) ->
                nameFlux.map(String::toUpperCase)
                        .filter(x -> x.length() > strLen)
                        .concatMap(splitToFluxWithDelay);


        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(fluxFn)
                .log(); // db or remote service call.
    }


    Flux<String> namesFlux_transform_defaultEmpty(int strLen) {//length greater than length

        Function<Flux<String>, Flux<String>> fluxFn = (nameFlux) ->
                nameFlux.map(String::toUpperCase)
                        .filter(x -> x.length() > strLen)
                        .concatMap(splitToFluxWithDelay);


        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(fluxFn)
                .defaultIfEmpty("default")
                .log(); // db or remote service call.
    }

    Flux<String> namesFlux_transform_switchIfEmpty(int strLen) {//length greater than length

        Function<Flux<String>, Flux<String>> fluxFn = (nameFlux) ->
                nameFlux.map(String::toUpperCase)
                        .filter(x -> x.length() > strLen)
                        .concatMap(splitToFluxWithDelay);

        var defaultFlux = Flux.just("default")
                .transform(fluxFn);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(fluxFn)
                .switchIfEmpty(defaultFlux)
                .log(); // db or remote service call.
    }


    Flux<String> namesFlux_concat(int strLen) {//length greater than length


        var fluxA = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        var fluxB = Flux.fromIterable(List.of("anish", "piyaaa", "ruchita"));
        return Flux.concat(fluxA, fluxB).log();
    }

    Flux<String> namesMono_concatWith() {//length greater than length


        var monoA = Mono.just("Anish");
        var monoB = Mono.just("Piyaa");
        return monoA.concatWith(monoB)
                .transform(x -> x.map(String::toUpperCase))
                .log();
    }

    Flux<String> namesFlux_merge() {//length greater than length


        var fluxA = Flux.fromIterable(List.of("a", "b", "c"))
                .delayElements(Duration.ofMillis(100));
        var fluxB = Flux.fromIterable(List.of("d", "e", "f"))
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(fluxA, fluxB).log();

    }

    Flux<String> namesMono_mergeWith() {//length greater than length


        var monoA = Mono.just("A");
        var monoB = Mono.just("B");
        var monoC = Mono.just("C");
        var monoD = Mono.just("D");
        return monoA.mergeWith(monoB)
                .mergeWith(monoC)
                .mergeWith(monoD)

                .log();

    }


    Flux<String> namesFlux_mergeSequential() {


        var fluxA = Flux.fromIterable(List.of("a", "b", "c"))
                .delayElements(Duration.ofMillis(100));
        var fluxB = Flux.fromIterable(List.of("d", "e", "f"))
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(fluxA, fluxB).log();

    }


    Flux<String> namesFlux_zip() {


        var fluxA = Flux.fromIterable(List.of("a", "b", "c"));

        var fluxB = Flux.fromIterable(List.of("d", "e", "f"));


        return Flux.zip(fluxA, fluxB, (a, b) -> a + b).log();

    }


    Flux<String> namesFlux_zip4() {


        var fluxA = Flux.fromIterable(List.of("a", "b", "c"));

        var fluxB = Flux.fromIterable(List.of("d", "e", "f"));

        var flux123 = Flux.fromIterable(List.of("1", "2", "3"));

        var flux456 = Flux.fromIterable(List.of("4", "5", "6"));


        return Flux.zip(fluxA, fluxB, flux123, flux456)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();

    }

    Flux<String> namesFlux_zipWith() {


        var fluxA = Flux.fromIterable(List.of("a", "b", "c"));

        var fluxB = Flux.fromIterable(List.of("d", "e", "f"));

        return fluxA.zipWith(fluxB, (a, b) -> (a + b)).log();

    }


    Function<String, Flux<String>> splitToFlux = (x) -> Flux.fromIterable(x.chars()
            .mapToObj(i -> (char) i)
            .map(String::valueOf)
            .collect(Collectors.toList()));


    Function<String, Flux<String>> splitToFluxWithDelay = (x) -> Flux.fromIterable(x.chars()
            .mapToObj(i -> (char) i)
            .map(String::valueOf)
            .collect(Collectors.toList())).delayElements(Duration.ofMillis(1000));


    Mono<String> namesMono() {
        return Mono.just("anish").log(); // db or remote service call.
    }


    Mono<String> namesMono_map_filter(int len) {
        return Mono.just("anish")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .log(); // db or remote service call.
    }

    Mono<List<String>> namesMono_flatMap(int len) {
        return Mono.just("anish")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .flatMap(fnMono)
                .log(); // db or remote service call.
    }

    Mono<String> namesMono_zipWith() {//length greater than length

        var monoA = Mono.just("A");
        var monoB = Mono.just("B");
        return monoA.zipWith(monoB, (s, e) -> (s + "->" + e))
                .log();

    }


    public Function<String, Mono<List<String>>> fnMono = (input) -> Mono.just(List.of(input.split("")));


    public Flux<String> namesMono_flatMapMany(int len) {
        return Mono.just("anish")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .flatMapMany(splitToFluxWithDelay)
                .log(); // db or remote service call.
    }


    Mono<String> namesMono_map_filter_defaultIfEmpty(int len) {
        return Mono.just("anish")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .defaultIfEmpty("default")
                .log(); // db or remote service call.
    }


    Mono<String> namesMono_map_filter_switchIfEmpty(int len) {
        return Mono.just("anish")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .switchIfEmpty(Mono.just("DEFAULT"))
                .log(); // db or remote service call.
    }


    // Exception Flux


    public Flux<String> exception_flux() {


        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("There is an error")))
                .concatWith(Mono.just("D")).log();
    }


    // onErrorReturn , on ErrorResume, onErrorContinue will recover from error in the pipeline
    public Flux<String> exception_onErrorReturn() {


        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("There is an error")))
                .onErrorReturn("D").log();
    }


    public Mono<Object> exception_onErrorReturn_Mono() {


        return Mono.just("A")
                .map(value->{
                    throw new RuntimeException("Wrong Mono");
                })
                .onErrorReturn("abc")
               .log();
    }

    public Flux<String> exception_onErrorResume(Exception e) {


//        return Flux.just("A", "B", "C")
//                .concatWith(Flux.error(new IllegalStateException("There is an error")))
//                .onErrorResume(ex -> {
//                    log.error("Something went wrong : {}",ex.getMessage());
//                    return ex.getMessage().equals("There is an error");
//                }, (data) -> Mono.just("D")).log(); //using Predicate and Function in onErrorResume

//        return Flux.just("A", "B", "C")
//                .concatWith(Flux.error(new IllegalStateException("There is an error")))
//                .onErrorResume(ex -> {
//                    log.error("Something went wrong : {}",ex.getMessage());
//                    return Mono.just("D");
//                }).log(); //using Function<takes in exception, returns a Publisher>in onErrorResume


        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Something went wrong : {}", ex.getMessage());
                    if (ex instanceof IllegalStateException) {
                        return Flux.just("D","E");
                    }
                    return Flux.error(ex);
                }).log(); //using Function<takes in exception, returns a Publisher>in onErrorResume
    }

    public Mono<Object> exception_onErrorResume_Mono(Exception e) {

        return Mono.just("A")
                .map(val->{
                    throw new RuntimeException(e.getMessage());
                })
                .onErrorResume(ex -> {
                    log.error("Something went wrong : {}", ex.getMessage());
                    if (ex instanceof RuntimeException) {
                        return Mono.just("D");
                    }
                    return Mono.error(ex);
                }).log(); //using Function<takes in exception, returns a Publisher>in onErrorResume
    }

    public Flux<String> exception_onErrorContinue() {// Just drops the error element and allows the rest of the stream to complete

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, res) -> {
                    if (ex instanceof IllegalStateException) {
                        log.error("Error of type IllegalStateException: {}", res);
                    }
                })
                .log();
    }

    public Mono<String> exception_onErrorContinue_Mono(String input) {// Just drops the error element and allows the rest of the stream to complete

        return Mono.just(input)
                .map(name -> {
                    if (name.equals("abc"))
                        throw new RuntimeException("Exception occurred");
                    return name;
                })
                .onErrorContinue((ex, res) -> {
                    if (ex instanceof RuntimeException) {
                        log.error("Error of type IllegalStateException: {}", res);
                    }
                })
                .log();
    }


    //onErrorMap and doOnError will not continue and throw the exception to the caller.
    public Flux<String> exception_onErrorMap() {// transform exception from one type to another.//Terminates the stream

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap((ex) -> {
                        log.error("Error of type IllegalStateException");
                        return new ReactorException(ex,ex.getMessage());
                })
                .log();
    }


    public Flux<String> exception_doOnError() {// //Terminates the stream(throws to the caller). Logs the exception doOnError(Consumer<? extends Throwable>)

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .doOnError((ex) -> log.error("Error of type IllegalStateException"))
                .log();
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        //service.namesFlux().subscribe(System.out::println);
        service.namesMono().subscribe(System.out::println);

    }
}
