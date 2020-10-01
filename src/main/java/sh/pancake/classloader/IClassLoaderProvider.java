/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.classloader;

import java.util.Iterator;

public interface IClassLoaderProvider {

    Iterator<ClassLoader> getLoaderIterator();
    
}
