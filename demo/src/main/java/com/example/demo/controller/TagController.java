package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Tag;
import com.example.demo.services.ProjectService;
import com.example.demo.services.TagService;
import com.example.demo.services.TaskService;

@Controller
public class TagController {
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private TaskService taskService;

	@RequestMapping(value = { "projects/addTag/{id}" }, method = RequestMethod.GET)
	public String addTagForm(Model model, @PathVariable Long id) {
		model.addAttribute("project", this.projectService.getProject(id));
		model.addAttribute("tag", new Tag());
		return "addTag";
	}
	
	@RequestMapping(value = { "projects/addTag/{id}" }, method = RequestMethod.POST)
	public String addTag(@ModelAttribute("tag") Tag tag,
						 @PathVariable Long id,
						 Model model) {
		Tag tagSalvata = tagService.saveTag(tag);
		tagSalvata.setProject(projectService.getProject(id));
		projectService.getProject(id).getTags().add(tagSalvata);
		tagService.saveTag(tagSalvata);
		projectService.saveProject(projectService.getProject(id));
		return "redirect:/projects/" + id;
	}
	
	@RequestMapping(value = { "projects/addTagToTask/{id}" }, method = RequestMethod.GET)
	public String addTagToTaskForm(Model model, @PathVariable Long id) {
		model.addAttribute("task", this.taskService.getTask(id));
		model.addAttribute("tag", new Tag());
		return "addTagToTask";
	}

	@RequestMapping(value = { "projects/addTagToTask/{id}" }, method = RequestMethod.POST)
	public String addTagToTask(@ModelAttribute("tag") Tag tag,
			@PathVariable Long id,
			Model model) {
		Tag tagSalvata = tagService.saveTag(tag);
		this.tagService.retrieveAllTasks(tagSalvata.getId()).add(this.taskService.getTask(id));
		this.tagService.saveTag(tagSalvata);
		this.taskService.saveTask(this.taskService.getTask(id));
		return "redirect:/projects/" + this.taskService.getTask(id).getProject().getId();	
	}
}
