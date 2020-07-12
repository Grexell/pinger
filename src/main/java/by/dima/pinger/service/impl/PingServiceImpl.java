package by.dima.pinger.service.impl;

import by.dima.pinger.service.PingService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Service
@Log
public class PingServiceImpl implements PingService {

    @Value("${URLS}")
    private List<String> urls;

    @Value("${DELAY}")
    private int delay;

    private WebClient webClient;

    public PingServiceImpl() {
        webClient = WebClient.builder().build();
    }

    @PostConstruct
    private void init() {
        urls.forEach(url -> schedulePing(url, delay));
    }

    @Override
    public void schedulePing(String url, Integer delay) {
        Flux.interval(Duration.ofMillis(delay))
                .flatMap(t -> webClient.get()
                        .uri(url)
                        .exchange()
                        .flatMap(response -> response.bodyToMono(String.class)))
                .subscribe(msg -> log.info(url + " response: " + msg));
    }
}
