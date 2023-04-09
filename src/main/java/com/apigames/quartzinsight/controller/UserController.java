package com.apigames.quartzinsight.controller;

import com.apigames.quartzinsight.entity.Friends;
import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.entity.UserGame;
import com.apigames.quartzinsight.entity.Users;
import com.apigames.quartzinsight.repository.FriendRepository;
import com.apigames.quartzinsight.repository.GameRepository;
import com.apigames.quartzinsight.repository.UserGameRepository;
import com.apigames.quartzinsight.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserGameRepository userGameRepository;

    @Autowired
    private GameRepository gameRepository;

    @GetMapping
    public ResponseEntity<List<Users>> getUsers(){
        List<Users> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUser(@PathVariable("id") long id){
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userID}/friends")
    public ResponseEntity<List<Users>> getUserFriends(@PathVariable("userID") long userID) {
        Optional<Users> user = userRepository.findById(userID);
        if (user.isPresent()){
            List<Users> friends = userRepository.findFriendsByUserId(userID);
            return ResponseEntity.ok(friends);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> addUser(@RequestBody Users users){
        List<Users> existingUser = userRepository.findUsersByEmail(users.getEmail());
        if (!existingUser.isEmpty()){
            return new ResponseEntity<>("User Already exist",HttpStatus.CONFLICT);
        }
        Users newUser = userRepository.save(users);
        return new ResponseEntity<>("User Added ",HttpStatus.OK);
    }

    @PostMapping("/{userId}/friends")
    public ResponseEntity<String> AddUserFriend(@PathVariable("userId") long userId, @RequestBody long friendId){
        Optional<Users> existingUser = userRepository.findById(userId);
        Users myFriend = userRepository.findByUserIdAndFriendId(userId, friendId);
        Optional<Users> friend = userRepository.findById(friendId);
        if (existingUser.isPresent() && myFriend == null && friend.isPresent() && userId != friendId){
            log.info("je suis la" );
            Friends newFriend = new Friends();
            Users user = existingUser.get();

            //############### Ajouter l'ami
            newFriend.setUser(user);
            newFriend.setFriends(friend.get());
            friendRepository.save(newFriend);

            //Ici je dois Ajouter l'utilisateur à la liste d'amis de l'ami ajouté
            Friends newReversedFriend = new Friends();
            newReversedFriend.setUser(friend.get());
            newReversedFriend.setFriends(user);

            friendRepository.save(newReversedFriend);

            return new ResponseEntity<>("New friend added ",HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not found or friend already exists",HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id){
        Users existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null){
            friendRepository.deleteByUserOrFriends(existingUser, existingUser);
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> deleteUserFriend(@PathVariable long userId, @PathVariable long friendId){
        Optional<Users> existingUser = userRepository.findById(userId);
        Optional<Users> existingFriend = userRepository.findById(friendId);
        if (existingUser.isPresent() && existingFriend.isPresent()){
            List<Users> listFriends = userRepository.findFriendsByUserId(userId);

            Optional<Users> friendUser = listFriends.stream()
                    .filter(friend -> friend.getId() == friendId)
                    .findFirst();
            if (friendUser.isPresent()){
                friendRepository.deleteByUserAndFriends(existingUser.get(), friendUser.get());
                friendRepository.deleteByUserAndFriends(friendUser.get(), existingUser.get());
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.notFound().build();
            }
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/games")
    public ResponseEntity<List<Games>> getUserGames(@PathVariable long userId){
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()){
            List<Games> gamesList = userGameRepository.findGamesByUserId(userId);

            return new ResponseEntity<>(gamesList, HttpStatus.OK);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/games")
    public ResponseEntity<String> addUserGame(@PathVariable("userId") long userId, @RequestBody long gameId){
        Optional<Users> user = userRepository.findById(userId);
        Games myGame = userRepository.findByUserIdAndGameId(userId, gameId);
        Optional<Games> game = gameRepository.findById(gameId);
        if (user.isPresent() && myGame == null && game.isPresent() && game.get().getAvailability() == true){
            log.info("je suis la" );
            UserGame newGame = new UserGame();

            //############### Ajouter un jeu
            newGame.setUser(user.get());
            newGame.setGames(game.get());
            userGameRepository.save(newGame);

            return new ResponseEntity<>("New game added ",HttpStatus.OK);
        } else {
            log.info("Bad request");
            return new ResponseEntity<>("User is not found or game already exists",HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<String> deleteUserGame(@PathVariable("userId") Long userId, @PathVariable("gameId") Long gameId) {
        Optional<Users> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            List<Games> listGames = userGameRepository.findGamesByUserId(userId);

            Optional<Games> userGame = listGames.stream()
                    .filter(games -> games.getId() == gameId)
                    .findFirst();


            if (userGame.isPresent()){
                userGameRepository.deleteByUserAndGames(userOptional.get(), userGame.get());
                return new ResponseEntity<>("Game successfully removed from user", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User doesn't have this game", HttpStatus.NOT_FOUND);
            }

        } else {
            return new ResponseEntity<>("User or game not found", HttpStatus.NOT_FOUND);
        }
    }

}
