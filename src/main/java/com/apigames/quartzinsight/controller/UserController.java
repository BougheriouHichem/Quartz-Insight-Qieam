package com.apigames.quartzinsight.controller;

import com.apigames.quartzinsight.entity.Friends;
import com.apigames.quartzinsight.entity.Games;
import com.apigames.quartzinsight.entity.UserGame;
import com.apigames.quartzinsight.entity.Users;
import com.apigames.quartzinsight.repository.FriendRepository;
import com.apigames.quartzinsight.repository.GameRepository;
import com.apigames.quartzinsight.repository.UserGameRepository;
import com.apigames.quartzinsight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
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
    public ResponseEntity<Users> addUser(@RequestBody Users users){
        List<Users> existingUser = userRepository.findUsersByEmail(users.getEmail());
        if (!existingUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Users newUser = userRepository.save(users);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/{userId}/friends")
    public ResponseEntity<Friends> AddUserFriend(@PathVariable("userId") long userId, @RequestBody Users friend){
        Optional<Users> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()){

            Users user = existingUser.get();
            List<Friends> friendsList = friendRepository.findAll();
            Friends newFriend = new Friends();

            //Vérifier s'il n'existe pas dans la liste d'amis
            for (Friends friendsLists: friendsList){
                if (friendsLists.getFriends().getEmail().equals(friend.getEmail())){
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            //Créer une nouvelle instance de Friends avec l'utilisateur et l'ami fournis
            newFriend.setUser(user);
            newFriend.setFriends(friend);
            Friends savedNewFriend = friendRepository.save(newFriend);

            //Ici je dois Ajouter l'utilisateur à la liste d'amis de l'ami ajouté
            Friends newReversedFriend = new Friends();
            newReversedFriend.setUser(friend);
            newReversedFriend.setFriends(user);
            friendRepository.save(newReversedFriend);

            return ResponseEntity.ok(savedNewFriend);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id){
        Users existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null){
           /* List<Friends> friendsList = friendRepository.findAllByUser_Id(id);
            for (Friends friends: friendsList){
                friendRepository.delete(friends);
            }*/
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
    public ResponseEntity<Games> addUserGame(@PathVariable("userId") long userId, @RequestBody Games game){
        Optional<Users> existingUser = userRepository.findById(userId);
        Optional<Games> existingGame = gameRepository.findById(game.getId());
        // Vérifier l'existance du User
        if (existingUser.isPresent() && existingGame.isPresent() && existingGame.get().getAvailability() == true) {
            // Vérifier l'existance du jeu dans la liste (UserGame)
            Users user = existingUser.get();
            List<UserGame> userGameList = userGameRepository.findAll();

            for (UserGame userGame: userGameList){
                if (userGame.getGames().getTitle().equals(game.getTitle())){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }

            // vérifier la disponibilité du jeu ensuite créer une instance UserGame et enregistrer le jeu
            UserGame newUserGame = new UserGame();
            newUserGame.setUser(user);
            newUserGame.setGames(game);

            userGameRepository.save(newUserGame);
            return new ResponseEntity<>(HttpStatus.OK);

        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
