package com.example.demoForJPA.controller;

import com.example.demoForJPA.Repository.UserRepo;
import com.example.demoForJPA.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Transactional
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    UserRepo userRepo;
//    @PostMapping("/api/user")
//    public ResponseEntity<User> saveUser(@RequestBody User user){
//        try {
//            return new ResponseEntity<>(userRepo.save(user), HttpStatus.CREATED);
//        } catch (Exception e) {
//            e.printStackTrace(); // Log the exception
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/api/user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try {
            User savedUser = userRepo.save(user);

            // Produce a Kafka message for user creation
            kafkaTemplate.send("user-topic", "User created: " + savedUser.getName());

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = userRepo.findAll();
        kafkaTemplate.send("user-topic", "Users retrieved: " + users.size() + " users");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id){
        Optional<User> user=userRepo.findById(id);
        if(user.isPresent()){
            kafkaTemplate.send("user-topic", "User retrieved: " + user.get().getName());
            return new ResponseEntity<>(user.get(),HttpStatus.OK);
        }
        else{
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/api/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id,@RequestBody User user){
        Optional<User> findUser=userRepo.findById(id);
        if(findUser.isPresent()){
            findUser.get().setEmail(user.getEmail());
            kafkaTemplate.send("user-topic", "User updated: " + findUser.get().getName());
            return new ResponseEntity<>(userRepo.save(findUser.get()),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @DeleteMapping("/api/users/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable long id){
//        Optional<User> findUser=userRepo.findById(id);
//        if(findUser.isPresent()){
//            userRepo.deleteById(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        else{
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
@DeleteMapping("/api/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable long id) {
    Optional<User> findUser = userRepo.findById(id);
    if (findUser.isPresent()) {
        userRepo.deleteById(id);
        // Produce a Kafka message for user deletion
        kafkaTemplate.send("user-topic", "User deleted: " + findUser.get().getName());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


}


