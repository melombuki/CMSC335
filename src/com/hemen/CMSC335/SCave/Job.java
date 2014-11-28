/*
 * Filename: Job.java
 * Date: 24 Nov. 2014
 * Last Modified: 26 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a job that can be attached
 *  to a creature. The job has requirements in order to start.
 *  It extends runnable and attempts to acquire a lock held
 *  by the creature it is attached to in order to run. Only
 *  one job can run for any one creature at a time.
 */

package com.hemen.CMSC335.SCave;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JProgressBar;

public class Job extends GameObject implements Runnable {
    private String name;
    private int creatureIndex;
    private double duration;
    private ArrayList<Requirement> requirements;
    private BlockingQueue<Artifact> ownedResources; //artifacts required to start this job
    private final ConcurrentHashMap<String, ArrayList<Artifact>> creatureResources; //parent creature resource pool
    private JProgressBar pm;
    private final ReentrantLock lock;
    private Condition canRun;
    private volatile boolean isRunning = false;
    private volatile boolean isCancelled = false;
    private volatile boolean isFinished = false;
    
    // Top level class constructor
    public Job(int index, String name, int creatureIndex, double duration,
            ArrayList<String> artifacts, ArrayList<Integer> amounts,
            ReentrantLock lock, Condition condition, 
            ConcurrentHashMap<String, ArrayList<Artifact>> creatureResources) {
        
        requirements = new ArrayList<Requirement>();
        pm = new JProgressBar();
        
        this.index = index;
        this.name = name;
        this.creatureIndex = creatureIndex;
        this.duration = duration;
        this.lock = lock;
        this.canRun = condition;
        this.creatureResources = creatureResources;
        this.ownedResources = new LinkedBlockingQueue<Artifact>();
        
        for(int i = 0; i < artifacts.size(); i++) {
            requirements.add(new Requirement(artifacts.get(i), amounts.get(i)));
        }
        
        new Thread(this).start();
    }

    // This inner class is used to store a required artifact
    //  as well as the amount needed before the job can begin.
    //  The string holds the 
    private class Requirement {
        private String artifactType;
        private int requiredAmount;
        
        // Constructor
        public Requirement(String artifactType, int number) {
            this.artifactType = artifactType;
            this.requiredAmount = number;
        }  
    }

    // This method contains all of the code to complete the job.
    //  This thread can be paused, restarted at the same point
    //  in the job, or cancelled and started from the beginning.
    @Override
    public void run() {
        
//        while(true) {...
        try {
            lock.lock();

//                if(checkRequirements()) {
//                    // Pull all of the resources from the creature and start working
//                    pullResources();
//                } else {
//                    continue; // try to get the lock again. There are not enough resources available
//                }
            
            // Set isRunning to true after acquiring the lock
            isRunning = true; 
            
            // Set up all of the initial times
            double time = System.currentTimeMillis();
            double startTime = time;
            double stopTime = time + (1000 * duration);
            double totalTime = stopTime - time;
            
            // Loop to until the job has finished
            while(time < stopTime) {
            	if(!isRunning) {
            		startTime = time - startTime; //stores elapsed time already completed
            		
            		// Reset the progress bar if the job was canceled entirely
            		if(isCancelled) {
            		    pm.setValue(0);
            		    
//                      // Return all resources to the creature resource pool
//            		    returnResources();

//            		    // Break out of loop still cancelled
//            		    break;
            		}
            		
            		// Wait until able to run again
            		canRun.signal(); // let other threads know this one ducked out
            		while(!isRunning) { canRun.awaitNanos(500); } // poll isRunning every 0.5 seconds
            		
            		// Reset the progress bar if the job was canceled entirely
                    if(isCancelled) {
//                        // Return all resources to the creature resource pool
//                        returnResources();
                        
                        // Reset the timers
                        time = System.currentTimeMillis();
                        startTime = time;
                        stopTime = time + (1000 * duration);
                        
//                        // Break out of loop still cancelled
//                        break;
                    }
            		 //Reset all of the time variables to mark former changes
            		else {
                		time = System.currentTimeMillis();
                		startTime = time - startTime; // in the past 
                		stopTime = (1000 * duration) + startTime;
                    }
            	} else {
                	// Slight delay
                    Thread.sleep(100);
                        
                    // Set the progress bar's value to reflect work done
                    pm.setValue((int)(((time - startTime) / totalTime) * 100));
                   
                    // Fade the bar from red to green as it gets closer to being done
                    pm.setForeground(new Color(Math.max(0, 1 - (float)((time - startTime) / totalTime)), // red
                    		                   Math.min(1, (float)((time - startTime) / totalTime)),     // green
                    		                   0f));                                                     // blue
        
                    // Set the current time
                    time = System.currentTimeMillis();
            	}
            }
            
            // Set the value to full, i.e. the job is done
            pm.setValue(100);
            
            // Set color to blue signaling that it is done
            pm.setForeground(new Color(0f, 0f, 1f));
            
            // Set the finished flag
            isFinished = true;
            
        } catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
		    // Signal that the lock is about to be free and free the lock
		    canRun.signal();
            lock.unlock();
        }
    }
    
    // This method returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Creature Index: " + creatureIndex + "\n" +
                  "Time: " + duration + "\n" +
                  "--Required Artifacts--\n" +
                  "Artifact: Amount Required\n");
        
        for(Requirement r : requirements) {
            sb.append(r.artifactType + ": " + r.requiredAmount + "\n");
        }
        
        return sb.toString();
    }
    
    // Add a requirement to the requirements array list
    public void addRequirement(String name, int number) {
        requirements.add(new Requirement(name, number));
    }
    
    // This method pauses this job and waits for the user
    //  to click the start button again before being able
    //  to be restarted. This job might have to wait until
    //  the lock can be acquired again. Instantly starting
    //  is not guaranteed.
    public void pause() {
    	isRunning = false;
    	isCancelled = false;
    }
    
    // This method allows the thread to be started when the 
    //  lock is acquired again. This job might have to wait
    //  until its turn in the reentrant lock's queue.
    public void start() {
    	isRunning = true;
    	synchronized(this) {notify();}
    	// Priority queue? Thread group?
    }
    
    // This method kills the this job thread and then and then
    //  starts it from the beginning. 
    public void cancel() {
    	isRunning = false;
    	isCancelled = true;
    	
        // Reset the progress bar if the job was canceled entirely
    	if(!isFinished) {
    	    pm.setValue(0);
    	}
    }
    
    private boolean checkRequirements() {
        for(Requirement r : requirements) {
            // Make sure the resource type is there and in the correct amount
            if(creatureResources.containsKey(r.artifactType)
                    && creatureResources.get(r.artifactType).size() >= r.requiredAmount) {
            } else {
                return false;
            }
        }
        
        return true; // only if there we can meet the requirements
    }
    
    // This method pulls all of the required resources from the parent creature's
    //  resource pool. When this is called, it must be guaranteed that the resources
    //  are actually in the creature's HashMap.
    private void pullResources() {
        System.out.println("Job " + this.index + " removed resources.");
        
        for(Requirement r : requirements) {
            // Pull the required amount of this type of artifact from creature's resources
            for(int i = 0; i < r.requiredAmount; i++) {
                ownedResources.add(creatureResources.get(r.artifactType).remove(0));
            }
        }
    }
    
    // This method returns the resources to the creature's resource pool.
    //  Because of this, I might want to implement a blocking queue. If
    //  any thing else in the game tries to add a resource to the pool
    //  at the same time, bad things will happen. Not an issue unless
    //  I implement creating new resources when jobs are complete. This
    //  would only be logical, and should probably happen.
    private void returnResources() {
        System.out.println("Job " + this.index + " is in returnResources().");
        
        // Return the resources to the creature's resource pool
        for(Artifact a : ownedResources) {
            creatureResources.get(a.getType()).add(a);
            ownedResources.remove(a);
        }
    }
    
    // Getters and setters
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the duration
     */
    public double getDuration() {
        return duration;
    }
    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
    /**
     * @return the artifacts
     */
    public ArrayList<Requirement> getRequirements() {
        return requirements;
    }
    /**
     * @return the creatureIndex
     */
    public int getCreatureIndex() {
        return creatureIndex;
    }
    /**
     * @param creatureIndex the creatureIndex to set
     */
    public void setCreatureIndex(int creatureIndex) {
        this.creatureIndex = creatureIndex;
    }
    /**
     * @return the pm
     */
    public JProgressBar getProgressBar() {
        return pm;
    }
    /**
     * @return lock
     */
    public ReentrantLock getLock() {
        return lock;
    }
    
}
