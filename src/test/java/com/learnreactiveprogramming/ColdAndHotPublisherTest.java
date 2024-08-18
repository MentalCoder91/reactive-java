package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class ColdAndHotPublisherTest {


    @Test
    void coldPublisher() {

        var flux = Flux.range(1, 10);

        // Gets the whole value from start to end.
        flux.subscribe(item -> System.out.println("Subscriber 1:" + item));

        flux.subscribe(item -> System.out.println("Subscriber 2:" + item));


    }


    @Test
    void hotPublisher() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        // the 2nd subscriber does not start from the beginning.
        ConnectableFlux<Integer> connectableFlux = flux.publish(); // publish and connect
        connectableFlux.connect();

        connectableFlux.subscribe(item -> System.out.println("Subscriber 1:" + item));
        delay(4000);
        connectableFlux.subscribe(item -> System.out.println("Subscriber 2:" + item));
        delay(10000);
    }


    @Test
    void hotPublisher_autoConnect() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        // Gets the whole value from start to end.
        Flux<Integer> hotSource = flux.publish().autoConnect(2); // publish and connect


        hotSource.subscribe(item -> System.out.println("Subscriber 1:" + item));
        delay(2000);
        hotSource.subscribe(item -> System.out.println("Subscriber 2:" + item));
        System.out.println("Two subscribers are connected");
        delay(2000);
        hotSource.subscribe(item -> System.out.println("Subscriber 3:" + item));
        delay(10000);
    }


    @Test
    void hotPublisher_refCount() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnSubscribe(subs->{
                    System.out.println("Subs attached...");
                })
                .doOnCancel(()->{
                    System.out.println("Cancelled received");
                });

        // Gets the whole value from start to end.
        Flux<Integer> hotSource = flux.publish().refCount(2); // publish and connect


        var disposable = hotSource.subscribe(item -> System.out.println("Subscriber 1:" + item));
        delay(2000);
        var disposable1 =hotSource.subscribe(item -> System.out.println("Subscriber 2:" + item));
        System.out.println("Two subscribers are connected");
        delay(2000);
        disposable.dispose();
        disposable1.dispose();

        hotSource.subscribe(item -> System.out.println("Subscriber 3:" + item));
        hotSource.subscribe(item -> System.out.println("Subscriber 4:" + item));
        delay(10000);
    }
}
