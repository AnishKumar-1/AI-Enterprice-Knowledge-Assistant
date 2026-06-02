package ai.assistance.controllers;

import ai.assistance.dtos.userDto.LoginRequest;
import ai.assistance.dtos.userDto.LoginResponse;
import ai.assistance.dtos.userDto.RegistrationRequest;
import ai.assistance.dtos.userDto.RegistrationResponse;
import ai.assistance.services.authentication.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    //register user with firstName, lastName, email and password
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest){
        System.out.println("registerUser"+ registrationRequest);
        return ResponseEntity.ok(userService.register_user(registrationRequest));
    }

    //Login user with email and password
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.userLogin(request));
    }

}
