/*
 * Filename: Project1.java
 * Date: 1 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class contains the main method for the
 *  project and starts the actual program.
 */

package com.hemen.CMSC335.SCave;

import javax.swing.SwingUtilities;

import java.io.*;

public class Project3 {
    
    // Main method, entry point into the program
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Game game = new Game();
                game.setVisible(true);
            }
        });
    }
}