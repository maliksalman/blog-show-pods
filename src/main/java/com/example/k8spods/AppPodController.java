package com.example.k8spods;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class AppPodController {

    private final AppPodService appPodService;

    @GetMapping("/pods")
    private Flux<AppPod> listAppPods() {
        return Flux.fromIterable(appPodService.listAppPods());
    }
}
