package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.AuthenticationResponse;
import com.example.expensetrackingsystem.entities.User;
import com.example.expensetrackingsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final ImageService imageService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsImplService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(ImageService imageService ,JwtService jwtService, UserDetailsImplService userDetailsService, AuthenticationManager authenticationManager,PasswordEncoder  passwordEncoder) {
        this.imageService = imageService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;

    }


    public void register(User request) {
        // Check if the user already exists
        if (userDetailsService.findByUsername(request.getUsername()) != null || userDetailsService.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Username  or Email is already taken");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        System.out.println("password" +" "+ user.getPassword());


        // Save the user
        userDetailsService.save(user);
        // Create a bucket for the user
        imageService.createBucket(user.getUsername());



    }

    public AuthenticationResponse authenticate(String username, String password) {

         authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );


        User  user = userDetailsService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User doesnt exist");
        }


        return new AuthenticationResponse(jwtService.generateToken(user),user.getId() );
    }

}
