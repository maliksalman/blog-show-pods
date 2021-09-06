package com.example.k8spods;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AppDeployment {
    String app, team;
}
