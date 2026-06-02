package ai.assistance.services.authentication;

import ai.assistance.models.Users;
import ai.assistance.repositories.UsersRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepo usersRepo;

    public CustomUserDetailsService(UsersRepo usersRepo) {
      this.usersRepo=usersRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Users user=usersRepo.findByEmail(username);
       if(user==null){
           throw new UsernameNotFoundException("Invalid username or password");
       }
        return new User(user.getEmail(),user.getPassword(),authorities(user.getRole().getRoleName()));
    }

    //user roles
    private List<GrantedAuthority> authorities(String roleName){
        return  List.of(new SimpleGrantedAuthority(roleName));
    }
}
