package com.example.k8spods;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppPodController {

    private final AppPodService appPodService;

    @GetMapping("/pods")
    private List<AppPod> listAppPods() {
        return appPodService.listAppPods();
    }

    @GetMapping("/deployments")
    private List<AppDeployment> listAppDeployments() {
        return appPodService.getDeployments();
    }
}
