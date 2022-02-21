package rug.icdtools.icddashboard.services;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rug.icdtools.icddashboard.models.security.Authority;
import rug.icdtools.icddashboard.models.security.User;

/**
 * Created by fan.jin on 2016-10-31.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }*/
        //TODO user authentication
        if (!username.equals("user")){
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        List<GrantedAuthority> la=new LinkedList<>();
        la.add(new Authority("ROLE_USER"));
        la.add(new Authority("ROLE_CI"));
        return new User("user",passwordEncoder.encode("123"),la);
    }

    
}
