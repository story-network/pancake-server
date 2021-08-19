/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.net.URL;

import sh.pancake.server.command.DynamicCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.event.EventManager;

public class Extension<T> {

    private final String id;

    private final T metadata;
    
    private final URL url;

    private final EventManager eventManager;
    private final DynamicCommandDispatcher<PancakeCommandStack> commandDispatcher;

    public Extension(String id, URL url, T metadata) {
        this.id = id;

        this.metadata = metadata;
        this.url = url;

        this.eventManager = new EventManager();
        this.commandDispatcher = new DynamicCommandDispatcher<>();
    }
    
    public T getMetadata() {
        return metadata;
    }

    public String getId() {
        return id;
    }
    
    public URL getURL() {
        return url;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    public DynamicCommandDispatcher<PancakeCommandStack> getCommandDispatcher() {
        return commandDispatcher;
    }
    
}
