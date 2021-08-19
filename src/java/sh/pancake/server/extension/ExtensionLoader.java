/*
 * Created on Thu Aug 12 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.io.IOException;

/**
 * Interface for loading extension.
 */
@FunctionalInterface
public interface ExtensionLoader<T> {

    /**
     * Load extension.
     * The implementation will be called once and disposed.
     * Behavior on calling multiple time is undefined.
     *
     * @param loader
     * @return
     * @throws ExtensionLoadException
     * @throws IOException
     */
    Extension<T> load() throws ExtensionLoadException, IOException;

}
