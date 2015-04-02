package com.mybus.exception;

import lombok.Getter;
import org.springframework.web.servlet.ModelAndView;

public class InvalidEventUrlException extends AbstractUserFriendlyRuntimeException {

    @Getter
    private ModelAndView modelAndView;

    public InvalidEventUrlException(String message, String userFriendlyMessage, ModelAndView modelAndView) {
        super(message, userFriendlyMessage);
        this.modelAndView = modelAndView;
    }

    public InvalidEventUrlException(String userFriendlyMessage, ModelAndView modelAndView) {
        super(userFriendlyMessage);
        this.modelAndView = modelAndView;
    }

    public InvalidEventUrlException(String userFriendlyMessage, Throwable cause, ModelAndView modelAndView) {
        super(userFriendlyMessage, cause);
        this.modelAndView = modelAndView;
    }

    public InvalidEventUrlException(String message,
                                    String userFriendlyMessage,
                                    Throwable cause,
                                    ModelAndView modelAndView) {
        super(message, userFriendlyMessage, cause);
        this.modelAndView = modelAndView;
    }
}
