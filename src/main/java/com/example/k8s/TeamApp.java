package com.example.k8s;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TeamApp {
    String name;
    String team;
    int readyInstances;
}
