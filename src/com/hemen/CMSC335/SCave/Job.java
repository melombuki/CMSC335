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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JProgressBar;

public class Job extends GameObject implements Runnable {
    private String name;
    private int creatureIndex;
    private double duration;
    private ArrayList<Requirement> requirements;
    private JProgressBar pm;
    private final ReentrantLock lock;
    private Condition canRun;
    private volatile boolean isRunning = true;
    private volatile boolean isCanceled = false;
    
    // Top level class constructor
    public Job(int index, String name, int creatureIndex, double duration,
            ArrayList<String> artifacts, ArrayList<Integer> amounts,
            ReentrantLock lock, Condition condition) {
        requirements = new ArrayList<Requirement>();
        pm = new JProgressBar();
        
        this.index = index;
        this.name = name;
        this.creatureIndex = creatureIndex;
        this.duration = duration;
        this.lock = lock;
        this.canRun = condition;
        
        for(int i = 0; i < artifacts.size(); i++) {
            requirements.add(new Requirement(artifacts.get(i), amounts.get(i)));
        }
        
        new Thread(this).start();
    }

    // This inner class is used to store a required artifact
    //  as well as the amount needed before the job can begin.
    private class Requirement {
        private String artifact;
        private int number;
        
        // Constructor
        public Requirement(String artifact, int number) {
            this.artifact = artifact;
            this.number = number;
        }  
    }

    // This method contains all of the code to complete the job.
    //  This thread can be paused, restarted at the same point
    //  in the job, or cancelled and started from the beginning.
    @Override
    public void run() {
        // Wait to acquire the lock before starting
        lock.lock();
        
        // Set isRunning to true after acquiring the lock
        isRunning = true; 
        
        try {
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
            		if(isCanceled) 
            		    pm.setValue(0);
            		
            		// Wait until able to run again
            		canRun.signal(); // let other threads know this one ducked out
            		while(!isRunning) { canRun.awaitNanos(500); } // poll isRunning every 0.5 sec
            		
            		// Reset all of the times to start over from zero
            		if(isCanceled) {
                        time = System.currentTimeMillis();
                        startTime = time;
                        stopTime = time + (1000 * duration);
                        totalTime = stopTime - time;
                        
                        // Reset the progress bar
                        pm.setValue(0);
                        
                        // Reset isCanceled
                        isCanceled = false;
                    } 
            		// Reset all of the time variables to mark former changes
            		else {
                		time = System.currentTimeMillis();
                		startTime = time - startTime; // in the past 
                		stopTime = (1000 * duration) + startTime;
                    }
            	}
            	
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
            
            // Set the value to full, i.e. the job is done
            pm.setValue(100);
            
            // Set color to blue signaling that it is done
            pm.setForeground(new Color(0f, 0f, 1f));
            
            // Signal to another thread that the lock is being freed up
            canRun.signal();
            
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
            sb.append(r.artifact + ": " + r.number + "\n");
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
    }
    
    // This method allows the thread to be started when the 
    //  lock is acquired again. This job might have to wait
    //  until its turn in the reentrant lock's queue.
    public void start() {
    	isRunning = true;
    	// Priority queue? Thread group?
    }
    
    // The method kills the this job thread and then and then
    //  starts it from the beginning. 
    public void cancel() {
    	isRunning = false;
    	isCanceled = true;
    	
        // Reset the progress bar if the job was canceled entirely              
        pm.setValue(0);
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
