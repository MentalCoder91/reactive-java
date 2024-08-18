package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;


@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe","anu");
    static List<String> namesList1 = List.of("adam", "jill", "jack");


    public Flux<String> explore_publishOn() {

        var namesFlux = Flux.fromIterable(namesList)
                .concatWith(Flux.just("anish", "ruchita"))
                .publishOn(Schedulers.parallel()) // addresses the blocking
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Data->" + s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);

    }


    public Flux<String> explore_subscribeOn() {

        var namesFlux = flux1(namesList)
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        var namesFlux1 = flux1(namesList1)
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    log.info("Data->" + s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);

    }


    public ParallelFlux<String> explore_parallel() {

        // Sequential
//       return flux1(namesList)
//                .publishOn(Schedulers.boundedElastic())
//                .log();

        int i = Runtime.getRuntime().availableProcessors();

        log.info("Number of cores:{}", i);

        return Flux.fromIterable(namesList)
                //.publishOn(Schedulers.boundedElastic())
                .parallel()// 1st change
                .runOn(Schedulers.parallel())// 2nd change
                .map(this::upperCase)
                .log();

    }


    public Flux<String> explore_parallel_usingFlatMap() {


        return Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)  // blocking call
                        .subscribeOn(Schedulers.parallel())
                ).log();

    }


    public Flux<String> explore_parallel_usingFlatMapSequential() {


        return Flux.fromIterable(namesList)
                .flatMapSequential(name -> Mono.just(name)
                        .map(this::upperCase)  // blocking call
                        .subscribeOn(Schedulers.parallel())
                ).log();

    }

    public Flux<String> explore_parallel_1() {

        var namesFlux = Flux.fromIterable(namesList)   // Using flatMap
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)  // blocking call
                        .map(s -> {
                            log.info("Data1->" + s);
                            return s;
                        })
                        .subscribeOn(Schedulers.parallel()));

        var namesFlux1 = Flux.fromIterable(namesList1)  // Using the parallel() and runOn(Scheduler.parallel())
                .parallel()// 1st change
                .runOn(Schedulers.parallel())// 2nd change
                .map(this::upperCase)
                .map(s -> {
                    log.info("Data2->" + s);
                    return s;
                });

        return namesFlux.mergeWith(namesFlux1);

    }

    private Flux<String> flux1(List<String> namesList1) {
        return Flux.fromIterable(namesList1)
                .map(this::upperCase);
    }

    private String upperCase(String name) {
        delay(1000);// blocking call
        return name.toUpperCase();
    }

}
