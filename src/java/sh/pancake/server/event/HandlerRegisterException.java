/*
 * Created on Wed Aug 11 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.event;

public class HandlerRegisterException extends Exception {

    public HandlerRegisterException(String message) {
        super(message);
    }

    public HandlerRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
