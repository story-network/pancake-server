/*
 * Created on Tue Aug 17 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

/**
 * Define exceptions when ExtensionLoader failed to load due to requirement.
 */
public class ExtensionLoadException extends Exception {
    
    public ExtensionLoadException(String message) {
        super(message);
    }

    public ExtensionLoadException(String message, Throwable t) {
        super(message, t);
    }

}
