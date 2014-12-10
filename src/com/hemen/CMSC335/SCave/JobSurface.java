/*
 * Filename: JobSurface.java
 * Date: 24 Nov. 2014
 * Last Modified: 26 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class creates and fills a JPanel with a visual
 *  representation of each job attached to all of the creatures
 *  in the game. Each job is placed in a separate panel within
 *  this surface. Each job panel contains the job owners name,
 *  the name of the job, a start, pause, and cancel button, as
 *  well as a progress bar to show how far along the job is.
 *  The progress bar goes from red to green as it nears completion
 *  and finally turns blue when the job has finished.
 */

package com.hemen.CMSC335.SCave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JobSurface extends JPanel implements ActionListener {
    private final Cave cave;
    
    // Constructor
    public JobSurface(Cave cave) {
        this.cave = cave;
        
        // Set basic properties of this JPanel
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.DARK_GRAY);
    }
    
    // This method takes a job and adds some of its pertinent information
    //  to a new JPanel. The panel is then added to this container.
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
        
        // Set the new panel's boarder
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        panel.getInsets().set(2, 2, 2, 2);
        
        // Add labels
        String label = ((Creature)(cave.searchByIndex(job.getCreatureIndex()))).getName();
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(100, 16));
        jLabel.setMinimumSize(new Dimension(100, 16));
        jLabel.setMaximumSize(new Dimension(100, 16));
        panel.add(jLabel, c);
        c.gridx++;
        jLabel = new JLabel(job.index + ": " + job.getName());
        jLabel.setPreferredSize(new Dimension(200, 16));
        jLabel.setMinimumSize(new Dimension(100, 16));
        jLabel.setMaximumSize(new Dimension(100, 16));
        panel.add(jLabel, c);
        c.gridx++;
        
        // Add the three buttons
        JButton button = new JButton("Start");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        button.addActionListener(this);
        button.setName(Integer.toString(job.getIndex()));
        panel.add(button, c);
        c.gridx++;
        button = new JButton("Pause");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        button.addActionListener(this);
        button.setName(Integer.toString(job.getIndex()));
        panel.add(button, c);
        c.gridx++;
        button = new JButton("Cancel");
        button.setPreferredSize(new Dimension(100, 18));
        button.setMinimumSize(new Dimension(100, 18));
        button.setMaximumSize(new Dimension(100, 18));
        button.addActionListener(this);
        button.setName(Integer.toString(job.getIndex()));
        panel.add(button, c);
        c.gridx++;
        
        // Add progress bar from job
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1f;
        panel.add(job.getProgressBar(), c);
        
        // Add the new job panel to this JPanel
        add(panel);
        
        validate();
    }
    
    // This method adds all of the jobs that are in the cave.
    public void updateSurface() {
    	this.removeAll();
    	
        for(Party party : cave.getParties()) {
            for(Creature creature : party.getCreatures()) {
                for(Job job : creature.getJobs()) {
                    createAndAddJobPanel(job);
                }
            }
        }
        
        validate();
    }

    // This method calls the appropriate method to the corresponding button pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Start")) {
			((Job)cave.searchByIndex(Integer.valueOf(((JButton) e.getSource()).getName()))).start();
		}
		else if(e.getActionCommand().equals("Pause")) {
			((Job)cave.searchByIndex(Integer.valueOf(((JButton) e.getSource()).getName()))).pause();
		}
		else if (e.getActionCommand().equals("Cancel")) {
			((Job)cave.searchByIndex(Integer.valueOf(((JButton) e.getSource()).getName()))).cancel();
		}
	}
}
