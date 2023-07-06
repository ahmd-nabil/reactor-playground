package nabil.reactorplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

/**
 * @author Ahmed Nabil
 */
public class CombiningReactiveTypes {
    @Test
    public void mergeFluxes() {
        Flux<String> firstNamesFlux = Flux.just("Ahmed", "Harvey", "Chandler");
        Flux<String> lastNamesFlux = Flux.just("Nabil", "Spectre", "Bing");
        Flux<String> fullNamesFlux = firstNamesFlux.mergeWith(lastNamesFlux); // all firstNames then all lastNames
        StepVerifier.create(fullNamesFlux)
                .expectNext("Ahmed")
                .expectNext("Harvey")
                .expectNext("Chandler")
                .expectNext("Nabil")
                .expectNext("Spectre")
                .expectNext("Bing")
                .verifyComplete();
    }

    @Test
    public void mergeFluxes_withDelay() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler")
                .delayElements(Duration.ofMillis(100));
        Flux<String> lastNamesFlux = Flux
                .just("Nabil", "Spectre", "Bing")
                .delayElements(Duration.ofMillis(100))
                .delaySubscription(Duration.ofMillis(50));  // just a little after firstNamesFlux to switch back and forth between them
        Flux<String> fullNamesFlux = firstNamesFlux.mergeWith(lastNamesFlux);
        StepVerifier.create(fullNamesFlux)
                .expectNext("Ahmed")
                .expectNext("Nabil")
                .expectNext("Harvey")
                .expectNext("Spectre")
                .expectNext("Chandler")
                .expectNext("Bing")
                .verifyComplete();
        /***
            Normally, a Flux will publish data as quickly as it possibly can. Therefore, you use a
            delayElements() operation on both
         and delaySubscription() on lastNameFlux to make it a little slower than firstName
            as mergeWith() does not guarantee switching back and forth between fluxes we will use zip()
         ***/
    }

    @Test
    public void zipFluxes() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler");
        Flux<String> lastNamesFlux = Flux
                .just("Nabil", "Spectre", "Bing");
        Flux<Tuple2<String, String>> fullNamesFlux = Flux.zip(firstNamesFlux, lastNamesFlux);
        StepVerifier
                .create(fullNamesFlux)
                .expectNextMatches(t -> t.getT1().equals("Ahmed") && t.getT2().equals("Nabil"))
                .expectNextMatches(t -> t.getT1().equals("Harvey") && t.getT2().equals("Spectre"))
                .expectNextMatches(t -> t.getT1().equals("Chandler") && t.getT2().equals("Bing"))
                .verifyComplete();
    }

    @Test
    public void zipFluxes_toObjects() {
        Flux<String> firstNamesFlux = Flux
                .just("Ahmed", "Harvey", "Chandler");
        Flux<String> lastNamesFlux = Flux
                .just("Nabil", "Spectre", "Bing");
        Flux<String> fullNamesFlux = Flux.zip(firstNamesFlux, lastNamesFlux, (first, last) -> first + " " + last);
        StepVerifier
                .create(fullNamesFlux)
                .expectNext("Ahmed Nabil")
                .expectNext("Harvey Spectre")
                .expectNext("Chandler Bing")
                .verifyComplete();
    }

    @Test
    public void firstWithSignalFlux() {
        Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
                .delaySubscription(Duration.ofMillis(100));
        Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");
        Flux<String> anotherFastFlux = Flux.just( "Bugatti", "McLauren", "ferrari");
        Flux<String> race = Flux.firstWithSignal(slowFlux, fastFlux, anotherFastFlux);
        StepVerifier
                .create(race)
                .expectNextMatches(v -> v.equals("hare") || v.equals("Bugatti"))
                .expectNextMatches(v -> v.equals("cheetah") || v.equals("McLauren"))
                .expectNextMatches(v -> v.equals("squirrel") || v.equals("ferrari"))
                .verifyComplete();
    }
}
