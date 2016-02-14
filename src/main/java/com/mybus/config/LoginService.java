package com.mybus.config;

import com.mybus.dao.UserDAO;
import com.mybus.model.User;
import com.mybus.service.TestDataCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * Created by skandula on 4/2/15.
 */
@Service
public class LoginService implements UserDetailsService {
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TestDataCreator testDataCreator;

    @PostConstruct
    public void createTestData() {
        testDataCreator.createTestData();
    }
    
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
