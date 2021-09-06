package com.example.k8spods;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Indexer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsApi;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppPodService {

    private final Indexer<V1Pod> indexer;
    private final SharedIndexInformer<V1Pod> informer;

    private final static String APP_LABEL = "app";
    private final static String TEAM_LABEL = "team";
    private final static String APP_NAMESPACE = "dev";
    private final AppsV1Api appsV1Api;

    public AppPodService(ApiClient client, SharedInformerFactory informerFactory) {

        CoreV1Api coreApi = new CoreV1Api(client);

        appsV1Api = new AppsV1Api(client);



        informer = informerFactory.sharedIndexInformerFor(params -> coreApi.listNamespacedPodCall(
                        APP_NAMESPACE,
                        null,
                        null,
                        null,
                        null,
                        APP_LABEL,
                        null,
                        params.resourceVersion,
                        null,
                        params.timeoutSeconds,
                        params.watch,
                        null),
                V1Pod.class,
                V1PodList.class);
        indexer = informer.getIndexer();
    }

    @PostConstruct
    public void init() {
        informer.run();
    }

    @PreDestroy
    public void destroy() {
        informer.stop();
    }

    @SneakyThrows
    public List<AppPod> listAppPods() {
        return indexer.list().stream()
                .map(this::toAppPod)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public List<AppDeployment> getDeployments() {
        return appsV1Api.listNamespacedDeployment(
                APP_NAMESPACE,
                "",
                null,
                null,
                null,
                null,
                1000,
                "",
                "",
                10,
                false
            )
            .getItems()
            .stream()
            .map(this::toAppDeployment)
            .collect(Collectors.toList());
    }

    private AppDeployment toAppDeployment(V1Deployment v1Deployment) {
        return AppDeployment.builder()
            .app(v1Deployment.getMetadata().getLabels().get(APP_LABEL))
            .team(v1Deployment.getMetadata().getLabels().get(TEAM_LABEL))
            .build()
            ;
    }

    private AppPod toAppPod(V1Pod v1Pod) {
        return AppPod.builder()
                .app(v1Pod.getMetadata().getLabels().get(APP_LABEL))
                .image(v1Pod.getSpec().getContainers().get(0).getImage())
                .name(v1Pod.getMetadata().getName())
                .team(v1Pod.getMetadata().getLabels().get(TEAM_LABEL))
                .startTime(v1Pod.getStatus().getStartTime())
                .build();
    }
}
