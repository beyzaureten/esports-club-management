package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Game;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("newGame", new Game());
        return "games";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("newGame") Game game, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/games";
        }
        gameService.saveGame(game);
        return "redirect:/games";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Game game, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/games";
        }
        gameService.saveGame(game);
        return "redirect:/games";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && "ADMIN".equals(loggedUser.getRole())) {
            gameService.deleteGame(id);
        }
        return "redirect:/games";
    }
}