package com.mybus.payment;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mybus.util.PropertiesUtils;

/**
 * 
 * @author Suresh K
 *
 */
public class GatewaySettings {
 
    @Autowired
    private Environment env;

    private Properties homeDirProperties;

    private static final String HOME_DIR_PROPS_FILENAME = ".mybus.mongo.properties";

    public GatewaySettings(final Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null when initializing "
                    + this.getClass().getName());
        }
        this.env = environment;
        this.homeDirProperties = getPropertiesFromHomeDir();
    }

    public String getProperty(final String propertyName) {
        if (homeDirProperties != null) {
            if (homeDirProperties.containsKey(propertyName)) {
                return homeDirProperties.getProperty(propertyName);
            }
        }
        return env.getProperty(propertyName, "");
    }

    private Properties getPropertiesFromHomeDir() {
        return PropertiesUtils.getPropertiesFromHomeDirFile(HOME_DIR_PROPS_FILENAME);
    }
    
}
