package nabil.reactorplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ahmed Nabil
 */
public class CreatingReactiveTypes {

    @Test
    public void createAFlux_just() {
        Flux<String> namesFlux = Flux.just("Ahmed", "Nabil");
        StepVerifier.create(namesFlux)
                .expectNext("Ahmed")
                .expectNext("Nabil")
                .verifyComplete();
    }

    @Test
    public void createAFlux_fromArray() {
        Integer[] arr = {1,2,3,4,5};
        Flux<Integer> integerFlux = Flux.fromArray(arr);
        StepVerifier.create(integerFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void createAFlux_fromIterable() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        Flux<Integer> integerFlux = Flux.fromIterable(list);
        StepVerifier.create(integerFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void createAFlux_range() {
        Flux<Integer> rangeFlux = Flux.range(5, 5);
        StepVerifier.create(rangeFlux)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .expectNext(8)
                .expectNext(9)
                .verifyComplete();
    }

    @Test
    public void createAFlux_interval() {
        Duration beforeFirstEmmit = Duration.ofSeconds(1);
        Duration betweenEmits = Duration.ofMillis(100);
        Flux<Long> intervalFlux = Flux.interval(beforeFirstEmmit, betweenEmits).take(5);
                StepVerifier.create(intervalFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }
}
