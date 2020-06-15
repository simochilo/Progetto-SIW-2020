package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Credentials;
import com.example.demo.model.User;
import com.example.demo.repository.CredentialsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.CredentialsService;
import com.example.demo.services.UserService;
import com.example.demo.validator.UserValidator;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidator userValidator;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SessionData sessionData;
    
    @Autowired
    CredentialsService credentialsService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    CredentialsRepository credentialsRepository;

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public String home(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "home";
    }

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
    public String me(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        model.addAttribute("user", loggedUser);
        model.addAttribute("credentials", credentials);

        return "userProfile";
    }

    /**
     * This method is called when a GET request is sent by the user to URL "/users/user_id".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String admin(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "admin";
    }
    
    @RequestMapping(value = { "/users/me/edit" }, method = RequestMethod.GET)
    public String showEditForm(Model model) {
    	model.addAttribute("user", sessionData.getLoggedUser());
    	model.addAttribute("credentials", sessionData.getLoggedCredentials());
    	return "edit";
    }
    
    @RequestMapping(value = { "/users/me/edit" }, method = RequestMethod.POST)
    public String edit(@ModelAttribute("user") User user,
    				   Model model) {
    	User loggedUser = sessionData.getLoggedUser();
    	loggedUser.setFirstname(user.getFirstname());
    	loggedUser.setLastname(user.getLastname());
    	userRepository.save(loggedUser);
        return "home";
    }
    
    @RequestMapping(value =  { "/admin/users" } , method = RequestMethod.GET)
    public String usersList(Model model) {
    	List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
    	model.addAttribute("credentialsList", allCredentials);
    	return "allUsers";
    }
    
    @RequestMapping(value = { "admin/delete/{credentials.username}" })
    public String deleteUser(@PathVariable(name = "username") String username) {
    	Credentials credentials = this.credentialsRepository.findByUsername(username).get();
    	User user = credentials.getUser();
    	this.userRepository.delete(user);
    	this.credentialsRepository.delete(credentials);
    	return "redirect:/admin/users";
    }

}

