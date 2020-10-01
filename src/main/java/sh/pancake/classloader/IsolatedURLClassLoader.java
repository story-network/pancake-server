/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.classloader;

import java.net.URL;

public class IsolatedURLClassLoader extends DynamicURLClassLoader {

    public IsolatedURLClassLoader(URL[] urls) {
        super(urls, ClassLoader.getPlatformClassLoader());
    }

}