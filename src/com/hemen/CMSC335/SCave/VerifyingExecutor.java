/*
 * Filename: VerifyingExecutor.java
 * Date: 14 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class manages the jobs in each owning objects' job
 *  list. It executes only a single job at a time. When the running
 *  job completes, the next job in the job list is run. It also initiates 
 *  removing the finished job panel from the job surface.
 */

package com.hemen.CMSC335.SCave;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VerifyingExecutor extends ThreadPoolExecutor {
	private final Cave cave;

	// Constructor
    public VerifyingExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Cave cave) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        
        this.cave = cave;
    }
    
    // This method runs after a job has finished. It runs the next job in the 
    //  job list of the owning creature. It also initiates removing the job 
    //  panel from the job surface.
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        
        if(t == null) {
            // Put it back on the queue if it was cancelled
            if(!(((Job) r).isFinished())) {
                // If it was because the resources weren't there, just get back in line
                if(((Job) r).isResources()) {
                    // It it was cancelled when it was running, it was removed so add it again
                    ((Job) r).getJobSurface().createAndAddJobPanel((Job) r);
                }
                // Try the job again when possible
                this.execute(r);
            } 
            // Remove the job from the cave
            else {
            	for(Artifact artifact : ((Job) r).getOwnedResources()) {
            		cave.remove(artifact);
            	}
            	cave.remove(((Job) r));
            }
        } else {
            System.out.println("There was an error returning from a thread.");
            System.out.println(t.getMessage());
        }
    }
}