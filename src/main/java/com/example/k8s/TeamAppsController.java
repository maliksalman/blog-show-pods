package com.example.k8s;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class TeamAppsController {

    private final TeamAppsService service;

    @GetMapping("/teams")
    private Set<String> listTeams() {
        return service.listTeams();
    }

    @GetMapping("/teams/{team}/apps")
    private List<TeamApp> listTeamApps(@PathVariable String team) {
        return service.listTeamApps(team);
    }
}
