package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.MyUserDetailsService;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final MyUserDetailsService myUserDetailsService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, MyUserDetailsService myUserDetailsService, RoleService roleService) {
        this.userService = userService;
        this.myUserDetailsService = myUserDetailsService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showAllUsers(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = myUserDetailsService.findByUsername(userDetails.getUsername());
//        model.addAttribute("authorizedUser", userDetails);
        model.addAttribute("authorizedUser", userService.showOneUser(user.getId()));

        model.addAttribute("newUser", new User());
        model.addAttribute("users", userService.showAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());


        return "admin";
    }

    @GetMapping("/{id}")
    public String showOneUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.showOneUser(id));
        return "user";
    }

    @GetMapping("/new")
    public String createNewUser(@ModelAttribute("user") User user) {
        return "new";
    }

    @PostMapping()
    public String save(@ModelAttribute("user") @Valid User user,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.showOneUser(id));
        return "admin";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        userService.update(user);
        return "redirect:/admin";
    }
//    @PatchMapping("/{id}")
//    public String update(@ModelAttribute("user") @Valid User user,
//                         BindingResult bindingResult, @PathVariable("id") int id) {
//        if (bindingResult.hasErrors()) {
//            return "admin";
//        }
//        userService.update(id, user);
//        return "redirect:/admin";
//    }
//    @PostMapping("/update")
//    public String updateUser(@ModelAttribute User user) {
//        userService.updateUser(user);
//        return "redirect:/admin";
//    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/userForAdmin")
    public String userForAdmin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User userSecurity =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User user = myUserDetailsService.findByUsername(userSecurity.getUsername());

        if (user != null) {
            model.addAttribute("user", userService.showOneUser(user.getId()));
            System.out.println(user.toString());
            return "/userForAdmin";
        } else return "Пользователя не существует";
    }
}