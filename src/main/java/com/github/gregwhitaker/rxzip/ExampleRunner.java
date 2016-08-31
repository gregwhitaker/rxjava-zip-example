package com.github.gregwhitaker.rxzip;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ExampleRunner {
    private static final Random RAND = new Random(System.currentTimeMillis());

    public static void main(String... args) throws Exception {

        // Emits random integers between 0-9
        Observable<Integer> obs1 = Observable.interval(1_000, TimeUnit.MILLISECONDS)
                .map(cnt -> RAND.nextInt(10))
                .subscribeOn(Schedulers.computation());

        // Emits random integers between 0-9
        Observable<Integer> obs2 = Observable.interval(1_000, TimeUnit.MILLISECONDS)
                .map(cnt -> RAND.nextInt(10))
                .subscribeOn(Schedulers.computation());

        // Emits random integers between 0-9
        Observable<Integer> obs3 = Observable.interval(1_000, TimeUnit.MILLISECONDS)
                .map(cnt -> RAND.nextInt(10))
                .subscribeOn(Schedulers.computation());

        final CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);

        // Zips the random integer events together and compares them to find a winner
        Observable.zip(obs1, obs2, obs3, (v1, v2, v3) -> {
            String result = String.format("Values: %s %s %s -> ", v1, v2, v3);

            if (v1 > v2 && v1 > v3) {
                result += "Winner: Observable1";
            } else if (v2 > v1 && v2 > v3) {
                result += "Winner: Observable2";
            } else if (v3 > v1 && v3 > v2) {
                result += "Winner: Observable3";
            } else if (v1 == v2 && v1 != v3) {
                result += "Tie: Observable1 and Observable2";
            } else if (v1 == v3 && v1 != v2) {
                result += "Tie: Observable1 and Observable3";
            } else if (v2 == v3 && v2 != v1) {
                result += "Tie: Observable2 and Observable3";
            } else {
                result += "Tie: Observable1, Observable2, and Observable3";
            }

            return result;
        }).subscribe(result -> {
            System.out.println(result);
            latch.countDown();
        });

        latch.await();
        System.exit(0);
    }
}
