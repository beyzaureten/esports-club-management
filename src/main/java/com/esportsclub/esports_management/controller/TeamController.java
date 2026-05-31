package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.model.Game;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.GameService;
import com.esportsclub.esports_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final GameService gameService;
    private final UserService userService;

    public TeamController(TeamService teamService,
                          GameService gameService,
                          UserService userService) {
        this.teamService = teamService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        Map<Integer, String> gameMap = gameService.getAllGames().stream()
                .collect(Collectors.toMap(Game::getId, Game::getName, (v1, v2) -> v1));

        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("gameMap", gameMap);
        model.addAttribute("newTeam", new Team());
        model.addAttribute("loggedUser", loggedUser);

        if ("COACH".equals(loggedUser.getRole())) {
            String coachTeamName = loggedUser.getTeamName();
            if (coachTeamName != null && !coachTeamName.isEmpty()) {
                List<User> coachTeamPlayers = userService.getMembersByTeamName(coachTeamName);
                model.addAttribute("coachTeamName", coachTeamName);
                model.addAttribute("coachTeamPlayers", coachTeamPlayers);
                model.addAttribute("coachHasTeam", true);
            } else {
                model.addAttribute("coachHasTeam", false);
            }
        }

        return "teams";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("newTeam") Team team, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/teams";
        }
        if (team.getStatus() == null || team.getStatus().isEmpty()) {
            team.setStatus("ACTIVE");
        }
        teamService.saveTeam(team);
        return "redirect:/teams";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Team team, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/teams";
        }
        if (team.getStatus() == null || team.getStatus().isEmpty()) {
            team.setStatus("ACTIVE");
        }
        teamService.saveTeam(team);
        return "redirect:/teams";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && "ADMIN".equals(loggedUser.getRole())) {
            teamService.deleteTeam(id);
        }
        return "redirect:/teams";
    }
}