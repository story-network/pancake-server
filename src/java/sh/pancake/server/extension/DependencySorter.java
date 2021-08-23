/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

public class DependencySorter<T> {

    private final Collection<Extension<T>> store;

    public DependencySorter(Collection<Extension<T>> store) {
        this.store = store;
    }

    public List<Extension<T>> sortedList() {
        return store.stream().sorted((a, b) -> {
            if (a.getDependencies().contains(b.getId())) {
                return 1;
            }

            return 0;
        }).collect(Collectors.toUnmodifiableList());
    }

}
