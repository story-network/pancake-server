/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceUtil {
    
    /**
     * Run tasks parallel, throws first Exception catched.
     *
     * @param service
     * @param tasks
     * @throws Exception
     */
    public static void all(ExecutorService service, List<Callable<Void>> tasks) throws Exception {
        var futureIter = service.invokeAll(tasks).iterator();
        while (futureIter.hasNext()) {
            futureIter.next().get();
        }
    }

}
