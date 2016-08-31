/*
 * Copyright 2016 Greg Whitaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gregwhitaker.rxzip;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Starts three Observables that emit a random integer once per second.  The Observables are then zipped together
 * and the Observable that emits the largest number is deemed the winner.
 */
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
