package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	public Project getProject(Long id) {
		Optional<Project> result = this.projectRepository.findById(id);
		return result.orElse(null);
	}
	
	@Transactional
	public Project saveProject(Project project) {
		return this.projectRepository.save(project);
	}
	
	@Transactional
	public void deleteProject(Project project) {
		this.projectRepository.delete(project);
	}
	
	@Transactional
	public Project shareProjectWithUser(Project project, User user) {
		project.addMember(user);
		user = this.userService.addToVisibleProjects(project, user.getId());
		return this.projectRepository.save(project);
	}
	
	@Transactional
	public List<Project> retrieveProjectsSharedWith(Long userId) {
		User user = this.userService.getUser(userId);
		return user.getVisibleProjects();
	}

	@Transactional
	public List<Project> retrieveProjectsOwnedBy(Long userId) {
		User user = this.userService.getUser(userId);
		return user.getOwnedProjects();
	}
	
	@Transactional
	public List<Project> getAllProjects() {
		List<Project> result = new ArrayList<>();
		Iterable<Project> iterable = this.projectRepository.findAll();
		for(Project project : iterable)
			result.add(project);
		return result;
	}
	
}
