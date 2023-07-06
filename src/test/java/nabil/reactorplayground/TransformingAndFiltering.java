package nabil.reactorplayground;

import lombok.Data;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.*;

/**
 * @author Ahmed Nabil
 */
public class TransformingAndFiltering {
    @Test
    public void flux_skipElements() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler")
                .skip(1);
        StepVerifier
                .create(firstNamesFlux)
                .expectNext("Harvey", "Chandler")
                .verifyComplete();
    }

    @Test
    public void flux_skipTime() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler")
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(2));
        StepVerifier
                .create(firstNamesFlux)
                .expectNext("Harvey")
                .expectNext("Chandler")
                .verifyComplete();
    }

    // opposite of skip, also has elements and duration
    @Test
    public void flux_take() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler")
                .take(1);
        StepVerifier
                .create(firstNamesFlux)
                .expectNext("Ahmed")
                .verifyComplete();
    }

    @Test
    public void flux_takeTime() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler")
                .delayElements(Duration.ofMillis(100))
                .take(Duration.ofMillis(250));
        StepVerifier
                .create(firstNamesFlux)
                .expectNext("Ahmed")
                .expectNext("Harvey")
                .verifyComplete();
    }

    /**
     * The skip() and take() operations can be thought of as filter operations where the
     * filter criteria are based on a count or a duration. For more general-purpose filtering
     * of Flux values, you’ll find the filter() operation
     * **/

    @Test
    public void flux_filter() {
        Flux<String> nationalParkFlux = Flux
                .just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
                .filter(np -> !np.contains(" "));
        StepVerifier
                .create(nationalParkFlux)
                .expectNext("Yellowstone", "Yosemite", "Zion")
                .verifyComplete();
    }

    @Test
    public void flux_distinct() {
        Flux<String> animalFlux = Flux
                .just("dog", "cat", "bird", "dog", "bird", "anteater")
                .distinct();
        StepVerifier.create(animalFlux)
                .expectNext("dog", "cat", "bird", "anteater")
                .verifyComplete();
    }

    @Test
    public void flux_map() {
        Flux<Player> playerFlux = Flux
                .just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .map(name -> {
                    String[] split = name.split(" ");
                    return new Player(split[0], split[1]);
                });
        StepVerifier
                .create(playerFlux)
                .expectNext(new Player("Michael", "Jordan"))
                .expectNext(new Player("Scottie", "Pippen"))
                .expectNext(new Player("Steve", "Kerr"))
                .verifyComplete();
    }

    @Test
    public void flux_flatmap_synchronous() {
        // flatmap does not guarantee order.
        Flux<Player> playerFlux = Flux
                .just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(
                        name -> Mono.just(name)
                                .map(
                                        s -> {
                                            String[] split = s.split(" ");
                                            return new Player(split[0], split[1]);
                                        })
                );
        for(int i=0; i<100; i++) {
            StepVerifier
                    .create(playerFlux)
                    .expectNext(new Player("Michael", "Jordan"))
                    .expectNext(new Player("Scottie", "Pippen"))
                    .expectNext(new Player("Steve", "Kerr"))
                    .verifyComplete();
        }
    }

    @Test
    public void flux_flatmap_asynchronous() {
        // flatmap does not guarantee order.
        Flux<Player> playerFlux = Flux
                .just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(
                        name -> Mono.just(name)
                                .map(
                                        s -> {
                                            String[] split = s.split(" ");
                                            return new Player(split[0], split[1]);
                                })
                                .subscribeOn(Schedulers.parallel()) // this what makes parallel
                );
        List<Player> players = new ArrayList<>(
                Arrays.asList(
                        new Player("Michael", "Jordan"),
                        new Player("Scottie", "Pippen"),
                        new Player("Steve", "Kerr")));
        StepVerifier
                .create(playerFlux)
                .expectNextMatches(players::contains)
                .expectNextMatches(players::contains)
                .expectNextMatches(players::contains)
                .verifyComplete();
    }


    @Test
    public void flux_buffer() {
        Flux<String> fruitFlux = Flux.just(
                "apple", "orange", "banana", "kiwi", "strawberry");
        Flux<List<String>> bufferedFlux = fruitFlux
                .buffer(3);
        StepVerifier.create(bufferedFlux)
                .expectNext(Arrays.asList("apple", "orange", "banana"))
                .expectNext(Arrays.asList("kiwi", "strawberry"))
                .verifyComplete();
    }

    @Test
    public void flux_collectList() {
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry");
        Flux<List<String>> bufferedFlux = fruitFlux.buffer();
        StepVerifier
                .create(bufferedFlux)
                .expectNext(Arrays.asList("apple", "orange", "banana", "kiwi", "strawberry"))
                .verifyComplete();
        // buffer() with no args => same as collectList()
        Mono<List<String>> listMono = fruitFlux.collectList();
        StepVerifier
                .create(listMono)
                .expectNext(Arrays.asList("apple", "orange", "banana", "kiwi", "strawberry"))
                .verifyComplete();
    }

    @Test
    public void flux_collectMap() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Map<Character, String>> mapMono=  animalFlux.collectMap(animal -> animal.charAt(0));
        StepVerifier
                .create(mapMono)
                .expectNextMatches(
                        map -> map.get('a').equals("aardvark")
                                && map.get('e').equals("eagle")
                                && map.get('k').equals("kangaroo")
                )
                .verifyComplete();

    }

    @Data
    private static class Player {
        private final String firstName;
        private final String lastName;
    }
}
