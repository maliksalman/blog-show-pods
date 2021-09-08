package com.example.k8spods;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Indexer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("async")
public class AsynchronousTeamAppsService implements TeamAppsService {

    private final Indexer<V1Deployment> indexer;
    private final SharedIndexInformer<V1Deployment> informer;

    public AsynchronousTeamAppsService(ApiClient client, SharedInformerFactory informerFactory, @Value("${namespace}") String namespace) {
        log.info("Creating asynchronous team-app service, Namespace={}", namespace);
        AppsV1Api appsV1Api = new AppsV1Api(client);
        informer = informerFactory.sharedIndexInformerFor(params -> appsV1Api.listNamespacedDeploymentCall(
                        namespace,
                        null,
                        null,
                        null,
                        null,
                        TEAM_LABEL,
                        null,
                        params.resourceVersion,
                        null,
                        params.timeoutSeconds,
                        params.watch,
                        null),
                V1Deployment.class,
                V1DeploymentList.class);
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

    private TeamApp toTeamApp(V1Deployment v1Deployment) {
        return TeamApp.builder()
                .name(v1Deployment.getMetadata().getLabels().get(APP_LABEL))
                .team(v1Deployment.getMetadata().getLabels().get(TEAM_LABEL))
                .readyInstances(v1Deployment.getStatus().getReadyReplicas())
                .build();
    }

    public Set<String> listTeams() {
        return indexer.list()
                .stream()
                .map(deployment -> deployment.getMetadata().getLabels().get("team"))
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public List<TeamApp> listTeamApps(String team) {
        return indexer.list()
                .stream()
                .filter(deployment -> deployment.getMetadata().getLabels().get("team").equals(team))
                .map(this::toTeamApp)
                .collect(Collectors.toList());
    }
}
