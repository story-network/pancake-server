/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ModInfo {
    
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description = "";

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("dependencies")
    private List<String> dependencies;

    @SerializedName("entrypoint")
    private String entryClassName;

    @SerializedName("mixin")
    private String mixinConfigName = "";

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getEntryClassName() {
        return entryClassName;
    }

    @Nullable
    public String getMixinConfigName() {
        return mixinConfigName;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public List<String> getAuthors() {
        return authors;
    }

    @Nullable
    public List<String> getDependencies() {
        return dependencies;
    }

}
