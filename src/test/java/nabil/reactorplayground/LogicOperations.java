package nabil.reactorplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Ahmed Nabil
 */
public class LogicOperations {
    @Test
    public void flux_all() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Boolean> allHasAMono = animalFlux.all(animal -> animal.contains("a"));
        StepVerifier
                .create(allHasAMono)
                .expectNext(true)
                .verifyComplete();

        Mono<Boolean> allHasRMono = animalFlux.all(animal -> animal.contains("r"));
        StepVerifier
                .create(allHasRMono)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void flux_any() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Boolean> anyHasRMono = animalFlux.any(animal -> animal.contains("r"));
        StepVerifier
                .create(anyHasRMono)
                .expectNext(true)
                .verifyComplete();

        Mono<Boolean> anyHasZMono = animalFlux.any(animal -> animal.contains("z"));
        StepVerifier
                .create(anyHasZMono)
                .expectNext(false)
                .verifyComplete();
    }
}
