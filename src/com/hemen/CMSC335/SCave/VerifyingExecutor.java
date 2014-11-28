package com.hemen.CMSC335.SCave;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VerifyingExecutor extends ThreadPoolExecutor {
	private Cave cave;

    public VerifyingExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Cave cave) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        
        this.cave = cave;
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        
        if(t == null) {
            // Put it back on the queue if it was cancelled
            if(!(((Job) r).isFinished())) {
                // If it was because the resources weren't there, just get back in line
                if((((Job) r).isResources())) {
                    // It it was cancelled when it was running, it was removed so add it again
                    ((Job) r).getJobSurface().createAndAddJobPanel((Job) r);
                }
                this.execute(r);
            } 
            // Remove the job from the cave
            else {
            	cave.remove(((Job) r));
            }
        }
        
        if(t != null) {
            System.out.println("There was an error returning from a thread.");
            System.out.println(t.getMessage());
        }
    }
}