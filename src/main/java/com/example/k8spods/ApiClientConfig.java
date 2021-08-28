package com.example.k8spods;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Configuration
public class ApiClientConfig {

    @Bean
    public ApiClient localApiClient() throws IOException {
        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(new FileReader(getConfigFile()));
        return ClientBuilder
                .kubeconfig(kubeConfig)
                .build();
    }

    private File getConfigFile() {
        String kubeConfigVar = System.getenv("KUBECONFIG");
        if (StringUtils.isNotBlank(kubeConfigVar)) {
            log.info("Using KUBECONFIG variable: Value='{}'", kubeConfigVar);
            return new File(kubeConfigVar);
        } else {
            File configDir = new File(System.getProperty("user.home"), ".kube");
            File configFile = new File(configDir, "config");
            log.info("Using home file: Path='{}'", configFile.getPath());
            return configFile;
        }
    }
}