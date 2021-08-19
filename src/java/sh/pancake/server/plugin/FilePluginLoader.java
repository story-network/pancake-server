/*
 * Created on Tue Aug 17 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;

import sh.pancake.server.Constants;
import sh.pancake.server.extension.Extension;
import sh.pancake.server.extension.ExtensionLoadException;
import sh.pancake.server.extension.ExtensionLoader;

public class FilePluginLoader implements ExtensionLoader<PluginInfo> {

    private final File file;
    
    public FilePluginLoader(File file) {
        this.file = file;
    }

    @Override
    public Extension<PluginInfo> load() throws ExtensionLoadException, IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry configEntry = zipFile.getEntry(Constants.PLUGIN_CONFIG);
            if (configEntry == null) throw new ExtensionLoadException(Constants.PLUGIN_CONFIG + " not found from file");
    
            try (InputStream configStream = zipFile.getInputStream(configEntry)) {
                PluginInfo info = new Gson().fromJson(new String(configStream.readAllBytes()), PluginInfo.class);
    
                return new Extension<>(info.getId(), file.toURI().toURL(), info);
            }
        }
    }

}
