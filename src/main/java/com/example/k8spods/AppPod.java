package com.example.k8spods;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class AppPod {
    String app;
    String image;
    String name;
    String team;
    OffsetDateTime startTime;
}
