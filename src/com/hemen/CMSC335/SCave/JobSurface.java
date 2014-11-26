package com.hemen.CMSC335.SCave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JobSurface extends JPanel {
    private Cave cave;
    
    public JobSurface(Cave cave) {
        this.cave = cave;
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.DARK_GRAY);
    }
    
    public void createAndAddJobPanel(Job job) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(800, 30));
        panel.setMinimumSize(new Dimension(800, 30));
        panel.setMaximumSize(new Dimension(800, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; // single row only
        c.weightx = 0.1f;
        c.gridy = 0; // start at first column
        c.fill = GridBagConstraints.VERTICAL;
        c.ipadx = 2;
        
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        panel.getInsets().set(2, 2, 2, 2);
        
        // Add labels
        String label = ((Creature)(cave.searchByIndex(job.getCreatureIndex()).get(0))).getName();
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(100, 16));
        jLabel.setMinimumSize(new Dimension(100, 16));
        jLabel.setMaximumSize(new Dimension(100, 16));
        panel.add(jLabel, c);
        c.gridx++;
        jLabel = new JLabel(job.getName());
        jLabel.setPreferredSize(new Dimension(100, 16));
        jLabel.setMinimumSize(new Dimension(100, 16));
        jLabel.setMaximumSize(new Dimension(100, 16));
        panel.add(jLabel, c);
        c.gridx++;
        
        // Add buttons
        JButton button = new JButton("Start");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        panel.add(button, c);
        c.gridx++;
        button = new JButton("Pause");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        panel.add(button, c);
        c.gridx++;
        button = new JButton("Cancel");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        panel.add(button, c);
        c.gridx++;
        
        // Add progress bar from job
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1f;
        panel.add(job.getProgressBar(), c);
        
        add(panel);
    }
    
    public void updateSurface() {
        for(Party party : cave.getParties()) {
            for(Creature creature : party.getCreatures()) {
                for(Job job : creature.getJobs()) {
                    createAndAddJobPanel(job);
                }
            }
        }
        
        validate();
    }
}
