package com.example.demo.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.model.Project;

@Component
public class ProjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Project.class.equals(clazz);
	}
	@Override
	public void validate(Object o, Errors errors) {
		Project project = (Project) o;
		String name = project.getName().trim();
		String description = project.getDescription();
		if(name.isEmpty())
			errors.rejectValue("name", "required");
		if(description.isEmpty())
			errors.rejectValue("description", "required");
	}

}
