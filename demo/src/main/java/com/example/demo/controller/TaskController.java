package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Comment;
import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.services.CredentialsService;
import com.example.demo.services.ProjectService;
import com.example.demo.services.TaskService;
import com.example.demo.services.UserService;

@Controller
public class TaskController {

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@RequestMapping(value = { "projects/addTask/{id}" }, method = RequestMethod.GET)
	public String addTaskForm(Model model, @PathVariable Long id) {
		model.addAttribute("project", this.projectService.getProject(id));
		model.addAttribute("task", new Task());
		return "addTask";
	}
	
	@RequestMapping(value = { "projects/addTask/{id}" }, method = RequestMethod.POST)
	public String addTask(@PathVariable Long id,
						  @ModelAttribute("task") Task task,
						  @RequestParam("username") String username,
						  Model model) {
		Project project = this.projectService.getProject(id);
		if(credentialsService.getCredentials(username) == null) {
			return "redirect:/projects/addTask/" + id;
		}
		User user = credentialsService.getCredentials(username).getUser();
		if(!projectService.retrieveProjectsSharedWith(user.getId()).contains(project)) {
			return "redirect:/projects/addTask/" + id;
		}
		Task taskSalvata = this.taskService.saveTask(task);
		taskSalvata.setUser(user);
		taskService.retrieveTasksAssignedTo(user.getId()).add(taskSalvata);
		this.taskService.getTask(taskSalvata.getId()).setProject(project);
		project.getTasks().add(taskSalvata);
		this.taskService.saveTask(taskSalvata);
		this.projectService.saveProject(project);
		return "redirect:/projects/" + id;
	}

	@RequestMapping(value = {"projects/editTask/{id}"}, method = RequestMethod.GET)
	public String editTaskForm(@PathVariable Long id, Model model) {
		model.addAttribute("task", taskService.getTask(id));
		return "editTask";
	}

	@RequestMapping(value = {"projects/editTask/{id}"}, method = RequestMethod.POST)
	public String editTask(@ModelAttribute("task") Task task,
			@PathVariable Long id,
			Model model) {
		Task taskRecuperata = this.taskService.getTask(id);
		taskRecuperata.setName(task.getName());
		taskRecuperata.setDescription(task.getDescription());
		taskService.saveTask(taskRecuperata);

		return "redirect:/projects/" + taskRecuperata.getProject().getId();
	}
	
	@RequestMapping(value = { "projects/{id}/deleteTask" }, method = RequestMethod.POST)
	public String deleteTask(Model model, @PathVariable Long id) {
		Long projectId = taskService.getTask(id).getProject().getId();
		this.taskService.deleteTask(taskService.getTask(id));
		return "redirect:/projects/" + projectId;
	}
	
	@RequestMapping(value = { "projects/addComment/{id}"}, method = RequestMethod.GET)
	public String addCommentForm(Model model, @PathVariable Long id) {
		model.addAttribute("task", this.taskService.getTask(id));
		return "addComment";
	}
	
	@RequestMapping(value = { "projects/addComment/{id}"}, method = RequestMethod.POST)
	public String addComment(@ModelAttribute ("task") Task task,
			@PathVariable Long id,
			@RequestParam("comment") Comment comment,
			Model model) {
		Task taskRecuperata = this.taskService.saveTask(task);
		taskRecuperata.getComments().add(comment);
		this.taskService.saveTask(taskRecuperata);
		
		return "redirect:/projects/" + this.taskService.getTask(id).getProject().getId();
	}
	
}
