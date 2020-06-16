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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Credentials;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.services.CredentialsService;
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

	@Autowired
	private CredentialsService credentialsService;

	/**
	 * Metodo che mostra i progetti posseduti dall'utente loggato
	 * @param model
	 * @return il metodo reindirizza alla pagina con la lista di progetti
	 */
	@RequestMapping(value = {"/projects" }, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = projectService.retrieveProjectsOwnedBy(loggedUser.getId());
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		return "projects";
	}

	/**
	 * Metodo che mostra i progetti condivisi con l'utente loggato
	 * @param model
	 * @return il metodo reindirizza alla pagina con la lista di progetti condivisi
	 */
	@RequestMapping(value = {"/sharedProjects" }, method = RequestMethod.GET)
	public String projectsSharedWithMe(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectsList = projectService.retrieveProjectsSharedWith(loggedUser.getId());
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectsList", projectsList);
		System.out.println("DIMENSIONE LISTA --> " + projectsList.size());
		return "sharedprojects";
	}

	/**
	 * Metodo che mostra un progetto dell'utente loggato tramite il suo id
	 * @param model
	 * @param projectId
	 * @return il metodo reindirizza alla pagina di presentazione del progetto
	 */
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

	/**
	 * Il metodo reindirizza alla form per creare un nuovo progetto
	 * @param model
	 * @return il template con il form da compilare
	 */
	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = new Project();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", project);
		return "addProject";
	}

	/**
	 * Il metodo riempe i campi del progetto, lo salva nel DB e lo aggiunge
	 * alla lista di progetti di cui è proprietario l'utente
	 * @param project
	 * @param projectBindingResult
	 * @param model
	 * @return
	 */
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

	@RequestMapping(value = { "projects/share/{id}" }, method = RequestMethod.GET)
	public String showShareProjectForm(Model model, @PathVariable Long id) {
		model.addAttribute("loggedUser", sessionData.getLoggedUser());
		model.addAttribute("project", this.projectService.getProject(id));
		return "shareProject";
	}

	@RequestMapping(value = { "projects/share/{id}" }, method = RequestMethod.POST)
	public String shareProject( @RequestParam("username") String username,
			@PathVariable Long id) {
		Project project = this.projectService.getProject(id);
		Credentials credentials = this.credentialsService.getCredentials(username);

		if(credentials != null) {
			this.projectService.shareProjectWithUser(project, credentials.getUser());
			return "redirect:/projects/" + id;
		}
		return "redirect:/home";
	}
}