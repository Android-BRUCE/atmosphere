/*
 * Copyright 2015 Async-IO.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.util;

import org.atmosphere.cpr.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper around a ForkJoinPool for JDK6 support.
 *
 * @author Jean-Francois Arcand
 */
public class ForkJoinPool extends AbstractExecutorService {

    private final static Logger logger = LoggerFactory.getLogger(ForkJoinPool.class);

    private final AbstractExecutorService forkJoinPool;

    private static boolean useForkJoinPool;
    static {
        try {
            Class.forName("java.util.concurrent.ForkJoinPool");
            useForkJoinPool = true;
        } catch (ClassNotFoundException e) {
            useForkJoinPool = false;
        }
    }

    public ForkJoinPool(boolean shared, final String threadName) {

        if (useForkJoinPool) {
            forkJoinPool = new JDK7ForkJoinPool(shared, threadName);
        } else {
            forkJoinPool = new JDK6ForkJoinPool(shared, threadName);

        }
        logger.info("Using ForkJoinPool  {}. Set the {} to -1 to fully use its power.", forkJoinPool.getClass().getName(), ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE);
    }

    @Override
    public void shutdown() {
        forkJoinPool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return forkJoinPool.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return forkJoinPool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return forkJoinPool.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return forkJoinPool.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        forkJoinPool.execute(command);
    }

}
