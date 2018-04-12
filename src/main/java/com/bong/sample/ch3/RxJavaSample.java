package com.bong.sample.ch3;

import org.apache.commons.lang3.tuple.Pair;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.just;
import static rx.Observable.timer;

/**
 * Created by NAVER on 2018-04-13.
 */
public class RxJavaSample {

    public static Observable<String> speak(String quote, long millisPerChar) {
        String[] tokens = quote.replaceAll("[:,]", "").split(" ");
        Observable<String> words = Observable.from(tokens);
        Observable<Long> absoluteDelay = words
                .map(String::length)
                .map(len -> len * millisPerChar)
                .scan((total, current) -> total + current);
        return words
                .zipWith(absoluteDelay.startWith(0L), Pair::of)
                .flatMap(pair -> just(pair.getLeft())
                        .delay(pair.getRight(), MILLISECONDS));
    }

    public static void main(String[] args) throws InterruptedException {
        // ch3
		Observable
				.just("Lorem", "ipsum", "dolor", "sit", "amet",
						"consectetur", "adipiscing", "elit")
				.map(word -> {
					timer(word.length(), SECONDS);
					return word;
				})
				.subscribe(System.out::println);
		TimeUnit.SECONDS.sleep(15);


        // ch3
        Observable<String> alice = speak(
                "To be, or not to be: that is the question", 110);
        Observable<String> bob = speak(
                "Though this be madness, yet there is method in't", 90);
        Observable<String> jane = speak("There are more things in Heaven and Earth, " +
                "Horatio, than are dreamt of in your philosophy", 100);

        Random rnd = new Random();
		Observable<Observable<String>> quotesObservable = just(
				alice.map(w -> "Alice: " + w),
				bob.map(w -> "Bob: " + w),
				jane.map(w -> "Jane: " + w))
				.flatMap(innerObs -> just(innerObs)
						.delay(rnd.nextInt(5), SECONDS));

        Observable<Observable<String>> quotes = just(
                alice.map(w -> "Alice: " + w),
                bob.map(w -> "Bob: " + w),
                jane.map(w -> "Jane: " + w))
                .map(innerObs -> innerObs
                        .delay(rnd.nextInt(5), SECONDS));
        Observable
                .switchOnNext(quotes)
                .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(100);
    }
}
