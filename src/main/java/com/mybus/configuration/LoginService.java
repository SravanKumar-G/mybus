package com.mybus.configuration;

import com.mybus.dao.UserDAO;
import com.mybus.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by skandula on 4/2/15.
 */
@Service
public class LoginService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserDAO userDAO;


    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userDAO.findOneByUserName(username);
        if(user == null) {
            return null;
        }
        UserDetails userDetails =  new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getUserName();
            }

            @Override
            public boolean isAccountNonExpired() {
                return user.isActive();
            }

            @Override
            public boolean isAccountNonLocked() {
                return user.isActive();
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return user.isActive();
            }

            @Override
            public boolean isEnabled() {
                return user.isActive();
            }
        };
        return userDetails;
    }
}
