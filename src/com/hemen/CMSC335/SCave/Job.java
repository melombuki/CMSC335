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
    private long me = -1;
    
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

    @Override
    public void run() {
        // Wait to acquire the lock before starting
    	me = Thread.currentThread().getId();
        lock.lock();
        isRunning = true; // set isRunning to true after acquiring the lock
        try {
            
            double time = System.currentTimeMillis();
            double startTime = time;
            double stopTime = time + (1000 * duration);
            double totalTime = stopTime - time;
            
            while(time < stopTime) {
            	if(!isRunning) {
            		startTime = time - startTime; //elapsed remaining
            		// Wait until able to run again
            		canRun.signal(); // let other threads know this one ducked out
            		while(!isRunning) { canRun.awaitNanos(500); } // poll isRunning every 0.5 sec
            		
            		// Reset all of the time variables
            		time = System.currentTimeMillis();
            		startTime = time - startTime; // in the past 
            		stopTime = (1000 * duration) + startTime;

            		// Signifies the thread is running again
            		//isRunning = true; 
            	}
            	
                Thread.sleep(100);
                    
                pm.setValue((int)(((time - startTime) / totalTime) * 100));
               
                // Fade the bar from red to green as it gets closer to being done
                pm.setForeground(new Color(Math.max(0, 1 - (float)((time - startTime) / totalTime)), // red
                		                   Math.min(1, (float)((time - startTime) / totalTime)),     // green
                		                   0f));                                                     // blue
    
                time = System.currentTimeMillis();
            }
            
            pm.setValue(100);
            
            // Set color to blue signaling that it is done
            pm.setForeground(new Color(0f, 0f, 1f));
            
            canRun.signal();
        } catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			canRun.signal();
            lock.unlock();
        }
    }
    
    // Returns a string with this objects information
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
    
    // Pauses this job class and waits for 
    public void pause() {
    	isRunning = false;
    }
    
    public void start() {
    	isRunning = true;
    	// Does nothing. Have to figure out how to suspend the current
    	//  job thread and start this one. Priority queue? Thread group?
    }
    
    public void cancel() {
    	// TODO: find a way to kill the current job thread and then
    	//  start it from the beginning. Blocking queue seems to be
    	//  the way to go. It would go in the owning creature object.
    	isRunning = false;
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
