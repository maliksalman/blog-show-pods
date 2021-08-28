package com.example.k8spods;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppPodService {

    private final CoreV1Api coreApi;
    private final static String APP_LABEL = "kubepaas.com/app";

    public AppPodService(ApiClient client) {
        coreApi = new CoreV1Api(client);
    }

    @SneakyThrows
    public List<AppPod> listAppPods() {

        V1PodList v1PodList = coreApi.listNamespacedPod("dev", null, null, null, null, APP_LABEL, null, null, null, null, null);

        return v1PodList.getItems().stream()
                .map(this::toAppPod)
                .collect(Collectors.toList());
    }

    private AppPod toAppPod(V1Pod v1Pod) {
        return AppPod.builder()
                .app(v1Pod.getMetadata().getLabels().get(APP_LABEL))
                .image(v1Pod.getSpec().getContainers().get(0).getImage())
                .name(v1Pod.getMetadata().getName())
                .startTime(v1Pod.getStatus().getStartTime())
                .build();
    }
}
