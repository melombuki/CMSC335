/*
 * Filename: Game.java
 * Date: 1 Nov. 2014
 * Last Modified: 11 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents everything to make the game.
 *  It is the top level JFrame for the GUI. It is also responsible
 *  for initializing the game by opening an input file and passing
 *  it to the lexer and parser. It creates the two different game
 *  surfaces with different visual representations of the game, 
 *  and links them together as appropriate.
 */

package com.hemen.CMSC335.Project1;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener {
    
    // Entry into the game multi-tree structure
    private Cave cave;
    
    // GUI components
    private GameTreeSurface gameTreeSurface;
    private IOSurface ioSurface;
    
    // Used to initialize game state from save file
    private final JFileChooser fc;
    private Lexer lexer;
    private boolean isInitialized = false; //used to allow initialization only once
    
    // Constructor
    public Game() {
        cave = new Cave();
        
        ioSurface = new IOSurface(cave, this);
        gameTreeSurface = new GameTreeSurface(ioSurface); //pass ioSurface to gameTreeSurface
        ioSurface.setGameTreeSurface(gameTreeSurface); //pass gameTreeSurface to ioSurface object
        
        fc = new JFileChooser(".");
        
        initGUI();
    }
    
    // This method initializes the GUI. It sets basic settings to the top
    //  level JFrame, and adds the two surfaces (JPanels) to itself.
    private void initGUI() {
        setTitle("Hemen's Killer Project 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        add(new JScrollPane(gameTreeSurface), BorderLayout.CENTER);

        ioSurface.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(new JScrollPane(ioSurface), BorderLayout.LINE_END);  
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
                Parser parse = new Parser(lexer, cave);
                parse.file();
                
                try {
                    lexer.close();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Failed to open file.");
                }
            }

            // Update the tree after parsing the input game state file
            gameTreeSurface.updateTreeView(cave);
            
            // Prevent any other file from being opened
            isInitialized = true;
        }
    }
}
