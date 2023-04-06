package com.apigames.quartzinsight.controller;

import com.apigames.quartzinsight.entity.Friends;
import com.apigames.quartzinsight.entity.Users;
import com.apigames.quartzinsight.repository.FriendRepository;
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

}
