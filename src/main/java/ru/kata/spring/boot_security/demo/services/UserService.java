package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    public User showOneUser(int id) {
        Optional<User> findOneUserById = userRepository.findById(id);
        return findOneUserById.orElse(null);
    }

    @Transactional
    public void save(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            System.out.println("Пользователь с таким именем уже есть в базе");
        } else {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    @Transactional
    public void update(User updatedUser) {
//        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}