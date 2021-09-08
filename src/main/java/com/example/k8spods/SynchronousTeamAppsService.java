package com.example.k8spods;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("!async")
public class SynchronousTeamAppsService implements TeamAppsService {

    private final AppsV1Api appsV1Api;
    private final String namespace;

    public SynchronousTeamAppsService(ApiClient client, @Value("${namespace}") String namespace) {
        log.info("Creating synchronous team-app service, Namespace={}", namespace);
        this.appsV1Api = new AppsV1Api(client);
        this.namespace = namespace;
    }

    private TeamApp toTeamApp(V1Deployment v1Deployment) {
        return TeamApp.builder()
                .name(v1Deployment.getMetadata().getLabels().get(APP_LABEL))
                .team(v1Deployment.getMetadata().getLabels().get(TEAM_LABEL))
                .readyInstances(v1Deployment.getStatus().getReadyReplicas())
                .build();
    }

    @SneakyThrows
    public Set<String> listTeams() {
        return appsV1Api.listNamespacedDeployment(
                        namespace,
                        null,
                        null,
                        null,
                        null,
                        TEAM_LABEL,
                        null,
                        null,
                        null,
                        null,
                        null)
                .getItems()
                .stream()
                .map(deployment -> deployment.getMetadata().getLabels().get("team"))
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public List<TeamApp> listTeamApps(String team) {
        return appsV1Api.listNamespacedDeployment(
                        namespace,
                        null,
                        null,
                        null,
                        null,
                        TEAM_LABEL + "=" + team,
                        null,
                        null,
                        null,
                        null,
                        null)
                .getItems()
                .stream()
                .map(this::toTeamApp)
                .collect(Collectors.toList());
    }
}
