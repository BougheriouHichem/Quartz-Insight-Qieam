package com.apigames.quartzinsight.controller;

import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/store")
public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/games")
    public ResponseEntity<List<Games>> getGames(){
        List<Games> gamesList = gameRepository.findAll();
        List<Games> gamesListAvailable = gameRepository.findGamesByAvailability();

        if (!gamesList.isEmpty()){
            return new ResponseEntity<>(gamesListAvailable, HttpStatus.OK);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/games")
    public ResponseEntity<Games> addGame(@RequestBody Games game){
        List<Games> existingGame = gameRepository.findGamesByTitle(game.getTitle());
        if (existingGame.isEmpty()){
            Games newGame = gameRepository.save(game);
            return ResponseEntity.ok(newGame);
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<Games> getGame(@PathVariable long gameId){
        Optional<Games> game = gameRepository.findById(gameId);
        if (game.isPresent() && game.get().getAvailability() == true){
            return new ResponseEntity<>(game.get(),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable long gameId){
        Games existingGame = gameRepository.findById(gameId).orElse(null);
        if (existingGame != null && existingGame.getAvailability() == true){
            existingGame.setAvailability(false);
            gameRepository.save(existingGame);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
