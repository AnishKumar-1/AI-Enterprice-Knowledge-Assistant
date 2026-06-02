package ai.assistance.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    //encrypt the user plain password by using the Becrpyt method
    public String encrypt_password(String password){
        return passwordEncoder.encode(password);
    }
}
