/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.provider;

/**
 * Provide associated object of key object
 */
public interface DependentProvider<K, V> {

    V get(K key);
    
}
