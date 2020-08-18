package com.github.yshameer.reactivewebfluxsample;

import com.github.yshameer.reactivewebfluxsample.entity.Profile;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WebSocketConfigurationTest {


    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();


    private final WebClient webClient = WebClient.builder().build();


    private Profile generateRandomProfile() {
        return new Profile(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "@email.com");
    }

    @Test
    public void testNotificationsOnUpdates() throws Exception {

        int count = 10;
        AtomicLong counter = new AtomicLong();
        URI uri = URI.create("ws://localhost:8080/ws/profiles");


        socketClient.execute(uri, (WebSocketSession session) -> {


            Mono<WebSocketMessage> out = Mono.just(session.textMessage("test"));


            Flux<String> in = session
                    .receive()
                    .map(WebSocketMessage::getPayloadAsText);


            return session
                    .send(out)
                    .thenMany(in)
                    .doOnNext(str -> counter.incrementAndGet())
                    .then();

        }).subscribe();


        Flux
                .<Profile>generate(sink -> sink.next(generateRandomProfile()))
                .take(count)
                .flatMap(this::write)
                .blockLast();

        Thread.sleep(1000);

        Assertions.assertThat(counter.get()).isEqualTo(count);
    }

    private Publisher<Profile> write(Profile p) {
        return
                this.webClient
                        .post()
                        .uri("http://localhost:8080/profiles")
                        .body(BodyInserters.fromValue(p))
                        .retrieve()
                        .bodyToMono(String.class)
                        .thenReturn(p);
    }
}
