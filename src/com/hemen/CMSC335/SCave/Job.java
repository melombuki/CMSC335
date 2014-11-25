package com.hemen.CMSC335.SCave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Job extends GameObject implements Runnable {
    private String name;
    private int creatureIndex;
    private double duration;
    private ArrayList<Requirement> requirements;
    
    // Top level class constructor
    public Job() {
        requirements = new ArrayList<Requirement>();
        
        new Thread(this).start();
    }
    
    public Job(int index, String name, int creatureIndex, double duration,
            ArrayList<String> artifacts, ArrayList<Integer> amounts) {
        requirements = new ArrayList<Requirement>();
        
        this.index = index;
        this.name = name;
        this.creatureIndex = creatureIndex;
        this.duration = duration;
        
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
        JProgressBar pm = new JProgressBar();
        
        JFrame jf = new JFrame("Demo");
        jf.add(new JLabel("Click the 'X' to close this window"), BorderLayout.PAGE_START);
        jf.add(pm, BorderLayout.CENTER);
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        
        double time = System.currentTimeMillis();
        double startTime = time;
        double stopTime = time + 1000 * duration;
        double totalTime = stopTime - time;
        
        while(time < stopTime) {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {}
            pm.setValue((int)(((time - startTime) / totalTime) * 100));
            
            // Fade the bar from red to green as it gets closer to being done
            pm.setForeground(new Color(1 - (float)((time - startTime) / totalTime), // red
            		                   (float)((time - startTime) / totalTime),     // green
            		                   0f));                                        // blue

            time = System.currentTimeMillis();
        }
        pm.setValue(100);
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
<<<<<<< HEAD
    
=======

>>>>>>> e37c5c5b90c9dd9cbc0146474ecaa35c670cbda7
}
