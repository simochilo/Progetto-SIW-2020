package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Credentials;
import com.example.demo.model.User;
import com.example.demo.services.CredentialsService;
import com.example.demo.services.UserService;
import static com.example.demo.model.Credentials.ADMIN_ROLE;

@Controller
public class MainController {

	@Autowired
	private UserService userService;

	@Autowired
	private CredentialsService credentialsService;

	public MainController() {}

	@RequestMapping(value = {"/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		if(credentialsService.getAllCredentials().size() == 0 ) {
			User user = new User("admin", "admin");
			Credentials credentials = new Credentials("admin", "admin");
			credentials.setUser(user);
			credentials.setRole(ADMIN_ROLE);
			credentialsService.saveCredentials(credentials);
			userService.saveUser(user);
		}
		return "index";

	}



}
