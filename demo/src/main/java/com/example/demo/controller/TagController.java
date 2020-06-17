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

@Controller
public class TagController {
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private ProjectService projectService;

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

}
