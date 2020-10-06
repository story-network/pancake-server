/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ModInfo {
    
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description = "";

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("entrypoint")
    private String modClassName;

    @SerializedName("mixin")
    private String mixinConfigName = "";

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getModClassName() {
        return modClassName;
    }

    public String getMixinConfigName() {
        return mixinConfigName;
    }

}
