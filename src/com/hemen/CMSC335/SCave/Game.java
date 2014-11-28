/*
 * Filename: Game.java
 * Date: 1 Nov. 2014
 * Last Modified: 25 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents everything to make the game.
 *  It is the top level JFrame for the GUI. It is also responsible
 *  for initializing the game by opening an input file and passing
 *  it to the lexer and parser. It creates the two different game
 *  surfaces with different visual representations of the game, 
 *  and links them together as appropriate.
 */

package com.hemen.CMSC335.SCave;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener {
    
    // Entry into the game multi-tree structure
    private Cave cave;
    
    // GUI components
    private JMenuBar menuBar;
    private JMenu windowMenu;
    private JMenuItem jTreeMI, bntMI;
    private GameTreeSurface gameTreeSurface;
    private IOSurface ioSurface;
    private JobSurface jobSurface;
    
    // Used to initialize game state from save file
    private final JFileChooser fc;
    private Lexer lexer;
    private boolean isInitialized = false; //used to allow initialization only once
    
    // Constructor
    public Game() {
        cave = new Cave(this);
        
        jobSurface = new JobSurface(cave);
        ioSurface = new IOSurface(cave, this);
        gameTreeSurface = new GameTreeSurface(cave, ioSurface); //pass ioSurface to gameTreeSurface
        ioSurface.setGameTreeSurface(gameTreeSurface); //pass gameTreeSurface to ioSurface object
        
        // Start file chooser at "."
        fc = new JFileChooser(".");
        
        // Setup the menu bar
        initMenuBar();
        setJMenuBar(menuBar);
        
        // Setup the main GUI and add view areas
        initGUI();
        
    }
    
    // This method sets up the menu bar and its options
    private void initMenuBar() {
        menuBar = new JMenuBar();
        windowMenu = new JMenu("View Options");
        
        jTreeMI = new JMenuItem("JTree View");
        jTreeMI.setToolTipText("Coming soon. View the tree as a JTree, fast but boring");
        jTreeMI.addActionListener(gameTreeSurface);
        
        bntMI = new JMenuItem("ButtonNodeTree View");
        bntMI.setToolTipText("Comming soon. View the tree as a ButtonNodeTree, slower but much more fun");
        bntMI.addActionListener(gameTreeSurface);
        
        windowMenu.add(jTreeMI);
        windowMenu.add(bntMI);
        
        menuBar.add(windowMenu);
    }
    
    // This method initializes the GUI. It sets basic settings to the top
    //  level JFrame, and adds the two surfaces (JPanels) to itself.
    private void initGUI() {
        setTitle("Hemen's Killer Project 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        add(new JScrollPane(gameTreeSurface), BorderLayout.CENTER);

        ioSurface.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        add(new JScrollPane(ioSurface), BorderLayout.LINE_END);  
        
        JScrollPane sp = new JScrollPane(jobSurface,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        sp.setPreferredSize(new Dimension(895, 200));
        add(sp, BorderLayout.SOUTH);
    }

    // Responds to the action command "Open File" fired from the 
    //  gameTreeSurface object.
    @Override
    public void actionPerformed(ActionEvent e) {
        
            if(e.getActionCommand().equals("Open File") && !isInitialized) {

            JButton button = ((JButton)e.getSource());
            button.setEnabled(false);
            
            // Open the file chooser and wait for file choice
            int returnVal = fc.showOpenDialog(Game.this);
            
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
//                //Testing only, comment out the jfilechooser if statement as well
//                File file = new File("test.txt");
                
                // Create the lexical analyzer to create tokens from file
                try {
                    lexer = new Lexer(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
                
                // Parse through the file and instantiate game objects
                Parser parse = new Parser(lexer, cave, jobSurface);
                parse.file();
                
                try {
                    lexer.close();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Failed to open file.");
                }
            }

//            // Update the tree after parsing the input game state file
//            gameTreeSurface.updateTreeView(cave);
//            
//            // Add all of the jobs to the jobSurface
//            jobSurface.updateSurface();
            
            // Prevent any other file from being opened
            isInitialized = true;
            
            validate();
        } 
        else if(e.getActionCommand().equals("CaveUpdate")) {
        	gameTreeSurface.updateTreeView(cave);
        	jobSurface.updateSurface();
        	validate();
        }
    }
}
