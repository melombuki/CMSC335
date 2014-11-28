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
import java.awt.Container;
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
    private double elapsedTime = 0;
    private ArrayList<Requirement> requirements;
    private BlockingQueue<Artifact> ownedResources; //artifacts required to start this job
    private final ConcurrentHashMap<String, ArrayList<Artifact>> creatureResources; //parent creature resource pool
    private JProgressBar pm;
    private final ReentrantLock runLock;
    private JobSurface jobSurface;
    private volatile boolean isPaused = false;
    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unpaused = pauseLock.newCondition();
    private volatile boolean isCancelled = false;
    private volatile boolean isFinished = false;
    private volatile boolean isResources = false;
    
    // Top level class constructor
    public Job(int index, String name, int creatureIndex, double duration,
            ArrayList<String> artifacts, ArrayList<Integer> amounts,
            ReentrantLock runLock, Condition condition, 
            ConcurrentHashMap<String, ArrayList<Artifact>> creatureResources,
            JobSurface jobSurface) {
        
        requirements = new ArrayList<Requirement>();
        pm = new JProgressBar();
        
        this.index = index;
        this.name = name;
        this.creatureIndex = creatureIndex;
        this.duration = duration;
        this.jobSurface = jobSurface;
        this.runLock = runLock;
        this.creatureResources = creatureResources;
        this.ownedResources = new LinkedBlockingQueue<Artifact>();
        
        for(int i = 0; i < artifacts.size(); i++) {
            requirements.add(new Requirement(artifacts.get(i), amounts.get(i)));
        }
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
        isResources = false;
        
        // Check to see if all of the resources needed are there
        if(checkRequirements()) {
            pullResources();
            isResources = true;
        } else {
            return;
        }
       
        // Set up all of the initial times
        double time = System.currentTimeMillis();
        double startTime = time;
        double stopTime = time + (1000 * duration);
        double totalTime = stopTime - time;
        
        // Loop to until the job has finished
        while(time < stopTime) {
        	if(isPaused) {
        		// Stores elapsed time already completed
        		elapsedTime = time - startTime; 
        		
        		// Pause and wait for unpause
        		pauseLock.lock();
        		try {
        		    while(isPaused) unpaused.await();
        		} catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
        		    pauseLock.unlock();
        		}
        		
        		if(isCancelled) {
        		    Container container = pm.getParent().getParent();
                    container.remove(pm.getParent());
                    container.validate();
                    container.repaint();
                    isCancelled = false;
                    isPaused = false;
                    elapsedTime = 0;
                    
                    returnResources();
                    
        		    return;
        		}
        		
        		// Reset all of the time variables to mark former changes
        		time = System.currentTimeMillis();
        		startTime = time - elapsedTime; // in the past 
        		stopTime = (1000 * duration) + startTime;
        	} 
        	// Reset the progress bar if the job was canceled entirely
        	else if (isCancelled) {
        	    // Remove the job display
        	    Container container = pm.getParent().getParent();
                container.remove(pm.getParent());
                container.validate();
                container.repaint();
                isCancelled = false;
                isPaused = false;
                elapsedTime = 0;
                
                returnResources();
                
                return;
                
            } else {
                try {
                    // Slight delay
                    Thread.sleep(100);
                    
                    if(isCancelled) {
                        Container container = pm.getParent().getParent();
                        container.remove(pm.getParent());
                        container.validate();
                        container.repaint();
                        isCancelled = false;
                        isPaused = false;
                        elapsedTime = 0;
                        
                        returnResources();
                        
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                    
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
        
        // Remove the job panel when completed after 2 seconds
        new Thread((new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // Remove the job display
                    Container container = pm.getParent().getParent();
                    container.remove(pm.getParent());
                    container.validate();
                    container.repaint();
                }
            }
        })).start();
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
    	isPaused = true;
    }
    
    // This method allows the thread to be started when the 
    //  lock is acquired again. This job might have to wait
    //  until its turn in the reentrant lock's queue.
    public void start() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
    
    // This method kills the this job thread and then and then
    //  starts it from the beginning.
    public void cancel() {
    	isCancelled = true;
    	isPaused = false;
    	
    	pauseLock.lock();
        try {
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    	
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
        for(Requirement r : requirements) {
            // Pull the required amount of this type of artifact from creature's resources
            for(int i = 0; i < r.requiredAmount; i++) {
                ownedResources.add(creatureResources.get(r.artifactType).remove(0));
            }
        }
    }
    
    // This method returns the resources to the creature's resource pool..
    private void returnResources() {        
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
     * @return runLock
     */
    public ReentrantLock getLock() {
        return runLock;
    }
    /**
     * @return the jobSurface
     */
    public JobSurface getJobSurface() {
        return jobSurface;
    }

    /**
     * @return the isFinished
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * @return the isResources
     */
    public boolean isResources() {
        return isResources;
    }
    
}
