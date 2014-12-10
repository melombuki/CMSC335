/*
 * Filename: Project1.java
 * Date: 1 Nov. 2014
 * Last Modified: 22 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class contains the main method for the
 *  project and starts the actual program.
 */

package com.hemen.CMSC335.SCave;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.*;

public class FinalProject {
    
    // Main method, entry point into the program
    public static void main(String[] args) throws IOException {
        try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Game game = new Game();
                game.setVisible(true);
            }
        });
    }
}