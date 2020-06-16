package com.example.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;
import com.example.demo.validator.ProjectValidator;

@Controller
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectValidator projectValidator;
	
	@Autowired
	private SessionData sessionData;
	
	@RequestMapping(value = {"/projects" }, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = projectService.retrieveProjectsOwnedBy(loggedUser.getId());
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		return "projects";
	}
	
	@RequestMapping(value = {"/sharedProjects" }, method = RequestMethod.GET)
	public String projectsSharedWithMe(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = projectService.retrieveProjectsSharedWith(loggedUser.getId());
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		return "sharedprojects";
	}
	
	@RequestMapping(value = { "/projects/{projectId}" }, method = RequestMethod.GET)
	public String project(Model model, @PathVariable Long projectId) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		if(project == null)
			return "redirect:/projects";
		
		List<User> members = userService.getMembers(projectId);
		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))
			return "redirect:/projects";
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("members", members);
		
		return "project";
	}
	
	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = new Project();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", project);
		return "addProject";
	}

	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.POST)
	public String createProject(@Valid @ModelAttribute("projectForm") Project project,
								BindingResult projectBindingResult,
								Model model) {
		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			this.projectService.retrieveProjectsOwnedBy(loggedUser.getId()).add(project);
			this.projectService.shareProjectWithUser(project, loggedUser);			
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "addProject";
	}
	
	@RequestMapping(value = { "/projects/edit/{projectId}" }, method = RequestMethod.GET)
    public String showEditForm(Model model, @PathVariable Long projectId) {
		model.addAttribute("projectFormEdit", this.projectService.getProject(projectId));
    	return "editProject";
    }
    
    @RequestMapping(value = { "/projects/edit/{projectId}" }, method = RequestMethod.POST)
    public String edit(@Valid @ModelAttribute ("projectFormEdit") Project project,
    					@PathVariable Long projectId,
    					BindingResult projectBindingResult,
    					Model model) {
    	
    	Project projectRecuperato = this.projectService.getProject(projectId);
    	projectValidator.validate(project, projectBindingResult);
    	
    	if(!projectBindingResult.hasErrors()) {
    		projectRecuperato.setName(project.getName());
    		projectRecuperato.setDescription(project.getDescription());
    		projectService.saveProject(projectRecuperato);
            return "redirect:/projects";
    	}
    	return "editProject";
    }
    
    @RequestMapping(value = { "projects/{id}/delete" }, method = RequestMethod.POST)
    public String deleteUser(Model model, @PathVariable Long id) {
    	this.projectService.deleteProject(this.projectService.getProject(id));
    	return "redirect:/projects";
    }
}