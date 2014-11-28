package com.hemen.CMSC335.SCave;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VerifyingExecuter extends ThreadPoolExecutor {

    public VerifyingExecuter(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        
        if(t == null) {
            // Put it back on the queue if it was cancelled
            if(!((Job) r).isFinished())
                this.execute(r);
        }
        if(t == null) {
            System.out.println("There was an error returning from a thread.");
        }
    }

}
