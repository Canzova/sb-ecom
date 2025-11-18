package com.ecommerce.controller;

import com.ecommerce.model.AppRole;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.request.LoginRequest;
import com.ecommerce.security.request.SignupRequest;
import com.ecommerce.security.response.MessageResponse;
import com.ecommerce.security.response.UserInfoResponse;
import com.ecommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
//import com.ecommerce.security.PasswordEncoder;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;

        try{

            // Step 1 : Authenticate User
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
        }

        // If authentication Fails
        catch(AuthenticationException exception){
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Bad Credentials");
            map.put("status", false);

            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        // Step 2 : USer is authenticated , so now put the authentication request into spring security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 3 : Get the user details (Userid, userName, roles and cookie) to send back to user in repsonse
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item-> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        System.out.println("AUTHENTICATED USER = " + userDetails);
        System.out.println("USERNAME FROM DB = " + userDetails.getUsername());
        System.out.println("ROLES = " + userDetails.getAuthorities());

//        return new ResponseEntity<UserInfoResponse>(response, HttpStatus.OK);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                jwtCookie.toString())
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return new ResponseEntity<>(new MessageResponse("Error : Username is already taken!"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(new MessageResponse("Error : Email is already taken!"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()) // we are using the same password encoder defined in webSecurityConfig.java
        );

        Set<String> strRoles = signupRequest.getRoles(); // User in sending in string
        Set<Role>roles= new HashSet<>();                 // In our db we are storing in Role class

        /*
            Why to use Db for fetching roles.txt
         */
        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Error : Role is not defined."));

            roles.add(userRole);
        }
        else
        {
            // admin---> ROLE_ADMIN
            strRoles.forEach(role->{
                switch(role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error : Role is not defined."));
                        roles.add(adminRole);
                        break;

                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(()-> new RuntimeException("Error : Role is not defined."));
                        roles.add(sellerRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("Error : Role is not defined."));

                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new MessageResponse("User registered Successfully"), HttpStatus.CREATED);
    }

    /*
        Spring injects the Authentication object automatically because it sees it as a method parameter in a web request handler.
        This is because Spring only injects Authentication automatically in controller method parameters — nowhere else.

        In controllers, Spring MVC automatically resolves method parameters like:
        Authentication
        Principal
        @AuthenticationPrincipal

        In service classes, Spring does NOT do this, because service methods are not tied to HTTP requests.
        So inside a service, Spring cannot automatically give you the current Authentication unless you manually
        read it from the SecurityContext.
     */

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if(authentication != null) return authentication.getName();
        return "";
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> currentUser(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item-> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);
        return ResponseEntity.ok(response);
    }

    // We have used post method because, post methods are also used for state changing actions.
    // User signe-out indicates an action that modifies the session
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = jwtUtils.generateCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You have been signed out!"));
    }
    
}
