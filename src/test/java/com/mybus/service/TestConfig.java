package com.mybus.service;

import com.mybus.configuration.CoreAppConfig;
import com.mybus.dao.CityDAO;
import com.mybus.dao.RoleDAO;
import com.mybus.dao.RouteDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(CoreAppConfig.class)
public class TestConfig {

    @Bean
    public RoleDAO roleDAO(){
        return mock(RoleDAO.class);
    }

    @Bean
    public RouteDAO routeDAO(){
        return mock(RouteDAO.class);
    }

    @Bean
    public CityDAO cityDAO(){
        return mock(CityDAO.class);
    }

    @Bean
    public RoleManager roleManager(){
        return new RoleManager();
    }

    @Bean
    public CityManager cityManager(){
        return new CityManager();
    }

}

