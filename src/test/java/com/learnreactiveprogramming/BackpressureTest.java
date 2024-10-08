package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
public class BackpressureTest {



    @Test
    void testBackPressure(){

        Flux<Integer> numberRange = Flux.range(1, 100).log();

//        numberRange.subscribe(num->{
//            log.info("The num:{}",num);
//        });

        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
               request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}",value);

                if(value ==2){
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.error("Inside cancel");
            }
        });

    }

    @Test
    void testBackPressure_1() throws InterruptedException {// Request 50 elements

        Flux<Integer> numberRange = Flux.range(1, 100).log();

//        numberRange.subscribe(num->{
//            log.info("The num:{}",num);
//        });

        CountDownLatch latch = new CountDownLatch(1);

        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}",value);

                if(value%2==0 || value<50){
                    request(1);
                }else{
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.error("Inside cancel");
                latch.countDown();
            }
        });

         assertTrue(latch.await(5L, TimeUnit.SECONDS));

    }


    @Test
    void testBackPressure_drop() throws InterruptedException {// Request 50 elements

        Flux<Integer> numberRange = Flux.range(1, 100).log();

//        numberRange.subscribe(num->{
//            log.info("The num:{}",num);
//        });

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureDrop(item->{
                    log.info("Dropped itms are: {}",item);
                })
                .subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}",value);

//                if(value%2==0 || value<50){
//                    request(2);
//                }else{
//                    cancel();
//                }


                if(value==2){
                    hookOnCancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.error("Inside cancel");
                latch.countDown();
            }
        });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));

    }

    @Test
    void testBackPressure_buffer() throws InterruptedException {// Request 50 elements

        Flux<Integer> numberRange = Flux.range(1, 100).log();

//        numberRange.subscribe(num->{
//            log.info("The num:{}",num);
//        });

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureBuffer(10,i->{     // requested 10 more elements in addition to 50 elements
                    log.info("Last buffered element is :{}",i);
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        //super.hookOnNext(value);
                        log.info("hookOnNext: {}",value);

                        if(value<50){
                            request(1);
                        }else{
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        //super.hookOnCancel();
                        log.error("Inside cancel");
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));

    }


    @Test
    void testBackPressure_error() throws InterruptedException {// Request 50 elements

        Flux<Integer> numberRange = Flux.range(1, 100).log();

//        numberRange.subscribe(num->{
//            log.info("The num:{}",num);
//        });

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureError()
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        //super.hookOnNext(value);
                        log.info("hookOnNext: {}",value);

                        if(value<50){
                            request(1);
                        }else{
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                        log.error("Exception is: {}",throwable.getMessage());
                    }

                    @Override
                    protected void hookOnCancel() {
                        //super.hookOnCancel();
                        log.error("Inside cancel");
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));

    }


}
