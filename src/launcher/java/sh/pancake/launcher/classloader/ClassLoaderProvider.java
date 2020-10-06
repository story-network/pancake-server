/*
 * Created on Fri Oct 02 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.classloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassLoaderProvider implements IClassLoaderProvider {

    private List<ClassLoader> subLoaderList;

    public ClassLoaderProvider() {
        this.subLoaderList = new ArrayList<>();
    }

    public boolean isAdded(ClassLoader loader) {
        return subLoaderList.contains(loader);
    }

    public boolean addSubLoader(ClassLoader loader) {
        if (isAdded(loader))
            return false;

        return subLoaderList.add(loader);
    }

    public boolean removeSubLoader(ClassLoader loader) {
        return subLoaderList.remove(loader);
    }

    @Override
    public Iterator<ClassLoader> getLoaderIterator() {
        return subLoaderList.iterator();
    }
    
}
