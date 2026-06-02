package ai.assistance.services.authentication;

import ai.assistance.dtos.userDto.LoginRequest;
import ai.assistance.dtos.userDto.LoginResponse;
import ai.assistance.dtos.userDto.RegistrationRequest;
import ai.assistance.dtos.userDto.RegistrationResponse;
import ai.assistance.helpers.JwtHelper;
import ai.assistance.helpers.UserHelper;
import ai.assistance.models.Roles;
import ai.assistance.models.Users;
import ai.assistance.repositories.RoleRepo;
import ai.assistance.repositories.UsersRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserHelper userHelper;
    private final UsersRepo usersRepo;
    private final ModelMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final RoleRepo roleRepo;
    private JwtHelper jwtHelper;

    public UserService(UserHelper userHelper,
                       UsersRepo usersRepo,
                       ModelMapper mapper,
                       AuthenticationManager authenticationManager,
                       RoleRepo roleRepo,
                       JwtHelper jwtHelper
    ) {
        this.userHelper = userHelper;
        this.usersRepo = usersRepo;
        this.mapper = mapper;
        this.authenticationManager=authenticationManager;
        this.roleRepo=roleRepo;
        this.jwtHelper=jwtHelper;
    }


    //user registration with default role = user
    public RegistrationResponse register_user(RegistrationRequest request) {
       Users existsUser=usersRepo.findByEmail(request.getEmail());
        if(existsUser!=null){
            throw new BadCredentialsException("User already exists with this email: "+ request.getEmail());
        }
        String encodedPassword = userHelper.encrypt_password(request.getCreatePassword());
        Users user = mapper.map(request, Users.class);
        user.setPassword(encodedPassword);
        Roles role = roleRepo.findByRoleName("ROLE_USER").orElseThrow(()->new RuntimeException("Role not found"));
        user.setRole(role);
        usersRepo.save(user);
        return new RegistrationResponse("Registration Successfully.");
    }

    //User Login method with email and password
    public LoginResponse userLogin(LoginRequest request){
       Authentication authentication=authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
               );
       SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority();

       String token=jwtHelper.generateToken(request.getEmail(),role);

       return new LoginResponse(request.getEmail(),"Login Successfully.",token);
    }
}
