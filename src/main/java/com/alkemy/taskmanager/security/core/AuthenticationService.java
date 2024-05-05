package com.alkemy.taskmanager.security.core;

import com.alkemy.taskmanager.security.data.TaskUserRepository;
import com.alkemy.taskmanager.security.domain.TaskUser;
import com.alkemy.taskmanager.security.domain.TaskUserRole;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.alkemy.taskmanager.utils.IdProvider.generateRandomId;

@Component
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TaskUserRepository taskUserRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password) throws AuthenticationException {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = (TaskUser) authentication.getPrincipal();
        return jwtService.createToken(user.getUsername(), 60);
    }

    public TaskUser registerUser(String username, String password){
        return register(username, password, TaskUserRole.USER);
    }

    public TaskUser registerAdmin(String user, String pass) {
        return register(user, pass, TaskUserRole.ADMIN);
    }

    public UserDetails loadUserByUsername(String username) {
        return taskUserRepository.findByUsername(username).orElseThrow();
    }

    private TaskUser register(String username, String password, TaskUserRole role) {
        if (taskUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        TaskUser newUser = new TaskUser(generateRandomId(), username, passwordEncoder.encode(password), role);
        return taskUserRepository.save(newUser);
    }
}