/*
 * Created on Tue Aug 17 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;

import sh.pancake.server.Constants;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionLoadException;
import sh.pancake.server.extension.ExtensionLoader;

public class FileModLoader implements ExtensionLoader<ModInfo> {

    private final File file;
    
    public FileModLoader(File file) {
        this.file = file;
    }

    @Override
    public Extension<ModInfo> load() throws ExtensionLoadException, IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry configEntry = zipFile.getEntry(Constants.MOD_CONFIG);
            if (configEntry == null) throw new ExtensionLoadException(Constants.MOD_CONFIG + " not found from file");
    
            try (InputStream configStream = zipFile.getInputStream(configEntry)) {
                ModInfo info = new Gson().fromJson(new String(configStream.readAllBytes()), ModInfo.class);
                List<String> dependencies = info.getDependencies() != null ? info.getDependencies() : new ArrayList<>();
    
                return new Extension<>(info.getId(), file.toURI().toURL(), dependencies, info);
            }
        }
    }

}
