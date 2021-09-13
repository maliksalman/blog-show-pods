package com.example.k8s;

import java.util.List;
import java.util.Set;

public interface TeamAppsService {

    String APP_LABEL = "app";
    String TEAM_LABEL = "team";

    Set<String> listTeams();
    List<TeamApp> listTeamApps(String team);
}
