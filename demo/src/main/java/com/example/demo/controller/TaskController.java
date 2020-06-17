package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Task;
import com.example.demo.services.TaskService;

@Controller
public class TaskController {

	@Autowired
	private TaskService taskService;

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

}
