package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Tag;
import com.example.demo.model.Task;
import com.example.demo.repository.TagRepository;

@Service
public class TagService {
	
	@Autowired
	private TagRepository tagRepository;
	
	@Transactional
	public Tag getTag(Long id) {
		Optional<Tag> result = this.tagRepository.findById(id);
		return result.orElse(null);
	}
	
	@Transactional
	public Tag saveTag(Tag tag) {
		return this.tagRepository.save(tag);
	}
	
	@Transactional
	public void deleteProject(Tag tag) {
		this.tagRepository.delete(tag);
	}
	
	@Transactional
	public List<Task> retrieveAllTasks(Long id) {
		Tag tag = this.tagRepository.findById(id).get();
		return tag.getTasks();
	}
}
