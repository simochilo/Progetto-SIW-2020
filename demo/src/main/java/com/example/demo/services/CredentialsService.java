package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.repository.CredentialsRepository;
import com.example.demo.model.Credentials;
import static com.example.demo.model.Credentials.ADMIN_ROLE;

@Service
public class CredentialsService {

	@Autowired
	protected CredentialsRepository credentialsRepository;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Transactional
	public Credentials getCredentials(Long id)  {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		return result.orElse(null);
	}

	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		if(credentials.getRole()!=ADMIN_ROLE)
			credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	@Transactional
	public void deleteCredentials(String username) {
		Credentials credentials = this.credentialsRepository.findByUsername(username).get();
		this.credentialsRepository.delete(credentials);
	}

	@Transactional
	public List<Credentials> getAllCredentials() {
		List<Credentials> result = new ArrayList<>();
		Iterable<Credentials> iterable = this.credentialsRepository.findAll();
		for(Credentials credentials : iterable)
			result.add(credentials);
		return result;
	}

}
