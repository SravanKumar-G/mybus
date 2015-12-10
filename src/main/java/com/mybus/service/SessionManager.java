package com.mybus.service;

import com.mybus.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * Created by skandula on 4/1/15.
 */
@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionManager {

    public static final String USER_SESSION_ATTR = "currentUser";
    public static final String SCREEN_SESSION_ATTR = "currentScreenSession";
    public static final String SESSION_TOKEN_COOKIE = "session_token";
    public static final String OAUTH_PROVIDER_COOKIE = "current_oauth_provider";
    public static final String PORTAL_COOKIE = "shodoggPortal";
    public static final String REC_SCREEN_USERNAME_COOKIE = "rsUserName";
    public static final String REC_SCREEN_AUTO_RECONNECT_COOKIE = "rsAutoRecon";

    public static final int DEFAULT_SESSION_EXPIRATION_SECS = 60 * 60 * 3; // 3 hours
    public static final int PORTAL_COOKIE_EXPIRATION_SECS = 60 * 60 * 24 * 90;  // 90 days


    @Getter
    @Setter
    private User currentUser;

}
