/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PluginInfo {
    
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("entrypoint")
    private String pluginClassName;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPluginClassName() {
        return pluginClassName;
    }

}
