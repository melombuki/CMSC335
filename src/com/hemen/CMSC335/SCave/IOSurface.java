/*
 * Filename: GameTreeSurface.java
 * Date: 1 Nov. 2014
 * Last Modified: 25 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class creates and fills a JPanel with a GUI components
 *  that allow a user to interact with the program. It allows the user
 *  to search the cave for game objects by index, name, or type. The
 *  default search is to search by id.
 */

package com.hemen.CMSC335.SCave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class IOSurface extends JPanel implements ActionListener, MouseListener {
    
    private final Cave cave;
    private GameTreeSurface gameTreeSurface;
    private Result result;
    private JButton currentSelection = null;
    private final JButton resetViewButton;
    private final Stack<Result> viewIndexHistory;
    private final ArrayList<GameObject> resultList;
    private final JButton previousViewButton;
    private final JTextField textField;
    private final JTextArea textArea;
    private final JComboBox<String> searchBox;
    private boolean isSearchBoxReady = false;
    private final JComboBox<String> searchBoxSub;
    private boolean isSearchBoxSubReady = false;
    private final JComboBox<String> sortByBox;
    private final String[] searchBoxOptions = {"Creatures", "Treasures", "Artifacts"};
    private final String[] creatureSortOptions = {"Empathy", "Fear", "Carry Capacity", "Index"};
    private final String[] treasureSortOptions = {"Weight", "Value", "Index"};
    private final String[] artifactSortOptions = {"Type", "Index"};
    private final ActionListener listener;
    private final JButton openButton;
    private final JButton searchButton;
    private final ButtonGroup group;
    private final JRadioButton idRadioButton;
    private final JRadioButton nameRadioButton;
    private final JRadioButton typeRadioButton;
    
    private enum SearchBy { id, name, type }; //determines type of search
    private SearchBy searchBy = SearchBy.id;  //holds the current search type
    
    
    // This inner class holds all of the game objects
    //  that were returned from any of the different
    //  types of searches. It holds and array list
    //  of GameObjects that represent the results.
    private class Result {
        private ArrayList<GameObject> results;
        
        // Constructor
        public Result() {
            results = new ArrayList<GameObject>();
        }
        
        // Constructor
        public Result(Result r) {
            results = new ArrayList<GameObject>();
            
            for(GameObject g : r.results)
                results.add(g);
        }
        
        // This method sets the results from a search.
        public void setResults(ArrayList<GameObject> g) {
            results.clear();
            results = g;
        }
        
        // This method sets the results from a search with a
        //  single game object as the results.
        public void setResults(GameObject g) {
            results.clear();
            results.add(g);
        }
        
        // This method is used to tell if the search results
        //  contained any game objects.
        public boolean isEmpty() {
            return results.isEmpty();
        }
        
        // The method is overridden to check for duplicate results.
        @Override
        public boolean equals(Object other) {
            if(other == null)
                return false;
            
            if(this.getClass() != other.getClass())
                return false;
            
            if(this.results.size() != ((Result) other).results.size())
                return false;
            
            Iterator<GameObject> i1 = results.iterator();
            Iterator<GameObject> i2 = ((Result) other).results.iterator();
            
            while(i1.hasNext()) {
                if(i1.next().index != i2.next().index) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    // Constructor for top-level class
    public IOSurface(Cave cave, ActionListener listener) {
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        // Create all of the GUI components
        this.cave = cave;
        result = new Result();
        textArea = new JTextArea(20, 20);
        searchBox = new JComboBox<String>(searchBoxOptions);
        searchBox.setSelectedIndex(-1);
        searchBoxSub = new JComboBox<String>();
        searchBoxSub.setSelectedIndex(-1);
        sortByBox = new JComboBox<String>();
        sortByBox.setSelectedIndex(-1);
        resetViewButton = new JButton("Reset Tree View");
        previousViewButton = new JButton("Previous View");
        viewIndexHistory = new Stack<Result>();
        resultList = new ArrayList<GameObject>();
        viewIndexHistory.add(new Result());
        viewIndexHistory.peek().setResults(cave);
        openButton = new JButton("Open File");
        searchButton = new JButton("Search");
        textField = new JTextField("Search me.", 15);
        group = new ButtonGroup();
        idRadioButton = new JRadioButton();
        nameRadioButton = new JRadioButton();
        typeRadioButton = new JRadioButton();
        this.listener = listener;
        
        initUI();
    }
    
    // Initializes the GUI components, that include a button
    //  to load a file, search the game structure, and a
    //  results display area.
    private void initUI() {
        GridBagConstraints c = new GridBagConstraints();

        // Add the open button
        c.weighty = 0;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        openButton.addActionListener(listener);
        add(openButton);
        
        // Add the various components to the search panel
        c.gridy++;
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.DARK_GRAY);
        GridBagConstraints searchC = new GridBagConstraints();
        searchC.gridy = 0;
        
        // Add the search field to the search panel
        searchButton.addActionListener(this);
        searchPanel.add(textField, searchC);
        
        // Add the search button to the search panel
        searchPanel.add(searchButton, searchC);
        
        // Add the radio buttons to the search panel
        JPanel radioPanel = initRadioButtons(searchPanel);
        searchC.gridy++;
        searchPanel.add(radioPanel, searchC);
        
        // Add the search panel to the IO panel
        add(searchPanel, c);
        
        // Add a label to designate the two types of searches
        c.gridy++;
        add(new JLabel(" "), c);
        c.gridy++;
        add(new JLabel(" --OR-- "), c);
        c.gridy++;
        add(new JLabel(" "), c);
        
        // Add the alternate searches here
        c.gridy++;
        JPanel comboBoxPanel = initComboBox();
        add(comboBoxPanel, c);
 
        // Add the results text area
        c.gridy++;
        textField.setBorder(BorderFactory.createLoweredBevelBorder());
        textArea.setEditable(false);
        add(new JScrollPane(textArea), c);
        
        // Create tree view manipulation panel
        c.gridy++;
        JPanel viewManipulationPanel = new JPanel(new GridLayout(0, 2));
        viewManipulationPanel.setBackground(Color.DARK_GRAY);
        
        // Add reset tree view button
        resetViewButton.addActionListener(this);
        viewManipulationPanel.add(resetViewButton);
        
        // Add limited "undo" button
        previousViewButton.addActionListener(this);
        viewManipulationPanel.add(previousViewButton);
        
        // Add tree view manipulation panel
        add(viewManipulationPanel, c);
   
//        // For debugging only, prints the size of whichever component
//        JButton button = new JButton("Get Size");
//        button.addActionListener(this);
//        c.gridy++;
//        add(button, c);

        // Add a blank label to take up all remaining y space
        c.weighty = 1;
        c.gridy++;
        add(new JLabel(), c);
    }
    
    // Initializes the radio buttons that are used to select the type of search
    //  performed on the cave tree structure.
    private JPanel initRadioButtons(JPanel searchPanel) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new GridLayout(0, 1));
        panel.setSize(240, panel.getHeight());
        
        JPanel idRBPanel = new JPanel();
        idRBPanel.setBackground(Color.DARK_GRAY);
        idRBPanel.setLayout(new BorderLayout());
        
        idRadioButton.setActionCommand("idRadioButton");
        idRadioButton.addActionListener(this);
        idRadioButton.setSelected(true);
        idRadioButton.setBackground(Color.DARK_GRAY);
        group.add(idRadioButton);
        idRBPanel.add(idRadioButton, BorderLayout.LINE_START);
        idRBPanel.add(new JLabel("Search by ID"), BorderLayout.CENTER);
        
        JPanel nameRBPanel = new JPanel();
        nameRBPanel.setBackground(Color.DARK_GRAY);
        nameRBPanel.setLayout(new BorderLayout());
        
        nameRadioButton.setActionCommand("nameRadioButton");
        nameRadioButton.addActionListener(this);
        nameRadioButton.setBackground(Color.DARK_GRAY);
        group.add(nameRadioButton);
        nameRBPanel.add(nameRadioButton, BorderLayout.LINE_START);
        nameRBPanel.add(new JLabel("Search by NAME"), BorderLayout.CENTER);
        
        JPanel typeRBPanel = new JPanel();
        typeRBPanel.setBackground(Color.DARK_GRAY);
        typeRBPanel.setLayout(new BorderLayout());
        
        typeRadioButton.setActionCommand("typeRadioButton");
        typeRadioButton.addActionListener(this);
        typeRadioButton.setBackground(Color.DARK_GRAY);
        group.add(typeRadioButton);
        typeRBPanel.add(typeRadioButton, BorderLayout.LINE_START);
        typeRBPanel.add(new JLabel("Search by TYPE"), BorderLayout.CENTER);
        
        panel.add(idRBPanel);
        panel.add(nameRBPanel);
        panel.add(typeRBPanel);
        
        return panel;
    }
    
    // Initializes the combo boxes that are used to select the secondary 
    //  type of search performed on the cave tree structure.
    private JPanel initComboBox() {
        Box b;
        
        // Create the panel
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBackground(Color.DARK_GRAY);
        
        // Add the search type box
        b = new Box(BoxLayout.X_AXIS);
        b.add(new JLabel("Search by: "));
        b.add(Box.createHorizontalGlue());
        searchBox.addActionListener(this);
        searchBox.setActionCommand("SearchBox");
        b.add(searchBox);
        panel.add(b);
        
        
        // Add the sub options box and set inactive
        b = new Box(BoxLayout.X_AXIS);
        b.add(new JLabel("Sub Options: "));
        b.add(Box.createHorizontalGlue());
        searchBoxSub.setEnabled(false);
        searchBoxSub.addActionListener(this);
        searchBoxSub.setActionCommand("SearchBoxSub");
        b.add(searchBoxSub);
        panel.add(b);
        
        // Add the sort options box and set inactive
        b = new Box(BoxLayout.X_AXIS);
        b.add(new JLabel("Sort Options: "));
        b.add(Box.createHorizontalGlue());
        sortByBox.setEnabled(false);
        sortByBox.addActionListener(this);
        sortByBox.setActionCommand("SortByBox");
        b.add(sortByBox);
        panel.add(b);
        
        return panel;
    }

    // Handles action command "Search" and setting of the search type
    //  by the respective action commands fired when the search by
    //  radio buttons are selected by the user. Also handles the 
    //  drop-down search method to search for all creatures, treasures,
    //  or artifacts within various game objects.
    @Override
    public void actionPerformed(ActionEvent e) {
        
//        // For debugging only, prints the size of whichever component
//        if(e.getActionCommand().equals("Get Size")) {
//            System.out.println(this.getParent().getParent().getParent().getSize());
//            return;
//        }
        
        // Go back to tree view with the cave as root
        if(e.getActionCommand().equals("Reset Tree View")) {
            if(viewIndexHistory.size() != 1) {
                viewIndexHistory.clear();
                result.setResults(cave);
                viewIndexHistory.add(new Result(result));
                gameTreeSurface.updateTreeView(result.results);
            }
        }
        // Go back to the last tree view
        else if(e.getActionCommand().equals("Previous View")) {
            if(viewIndexHistory.size() > 1) {
                viewIndexHistory.pop();
                gameTreeSurface.updateTreeView(viewIndexHistory.peek().results);
            }
        }
        // Search for a particular item
        else if(e.getActionCommand().equals("Search")) { //the search button was pressed

            try {
                // Perform the appropriate search type
                switch (searchBy) {
                case id:
                    result.setResults(cave.searchByIndex(Integer.parseInt(textField.getText())));
                    break;
                case name:
                    result.setResults(cave.searchByName(textField.getText()));
                    break;
                case type:
                    result.setResults(cave.searchByType(textField.getText()));
                    break;
                }
                
                if(currentSelection != null) {
                    currentSelection.setForeground(Color.BLACK);
                    currentSelection = null;
                } 
                
            } catch(InputMismatchException ex) {
                textArea.replaceRange("Must be an integer, try again.", 0, textArea.getDocument().getLength());
                return;
            } catch(NumberFormatException ex) {
                textArea.replaceRange("Please format your search correctly.", 0, textArea.getDocument().getLength());
                return;
            } catch(Exception ex) {
                textArea.replaceRange("Something went wrong, try again.", 0, textArea.getDocument().getLength());
                return;
            }
            
            // Handle displaying the results in the GUI
            if(!result.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                
                for(GameObject g : result.results)
                    sb.append(g.toString() + "\n");
                
                textArea.replaceRange(sb.toString(), 0, textArea.getDocument().getLength());
                
                // Update the view history stack 
                if(!viewIndexHistory.peek().equals(result)) {
                    viewIndexHistory.add(new Result(result));
                    gameTreeSurface.updateTreeView(viewIndexHistory.peek().results);
                }
            }
            else {
                textArea.replaceRange("That is not an element.", 0, textArea.getDocument().getLength());
            }
            
            // Reset the secondary search method if anything was set before
            resetSecondarySearch();
        }
        // Handle JComboBox searches
        else if(e.getActionCommand().equals("SearchBox")) {
            resultList.clear();
            searchBoxSub.removeAllItems(); //reset the sub-options
            sortByBox.removeAllItems(); //reset sort options
            searchBoxSub.setEnabled(false);
            sortByBox.setEnabled(false);
            isSearchBoxReady = false;
            isSearchBoxSubReady = false;
            
            if(searchBox.getSelectedIndex() != -1 && searchBox.getSelectedItem().equals("Creatures")) {
                
                for(Party party : cave.getParties()) {
                    searchBoxSub.addItem(party.getName());
                }
                
                isSearchBoxReady = true;
                searchBoxSub.setSelectedIndex(-1);
                searchBoxSub.setEnabled(true);
            }
            else if(searchBox.getSelectedIndex() != -1 && 
            		(searchBox.getSelectedItem().equals("Treasures") || 
            				searchBox.getSelectedItem().equals("Artifacts"))) {
            	//TODO: figure out how to add cave game objects to the result list gracefully
               // searchBoxSub.addItem(cave.getName());
                
                for(Party party : cave.getParties()) {
                    for(Creature creature : party.getCreatures()) {
                        searchBoxSub.addItem(creature.getName());
                    }
                }
                
                isSearchBoxReady = true;
                searchBoxSub.setSelectedIndex(-1);
                searchBoxSub.setEnabled(true);
            }
        }
        else if(e.getActionCommand().equals("SearchBoxSub")) {
            resultList.clear();
            sortByBox.removeAllItems(); //reset the sort options
            sortByBox.setEnabled(false);
            isSearchBoxSubReady = false;
            
            if(searchBoxSub.getSelectedIndex() != -1  && isSearchBoxReady) {
	            if( searchBox.getSelectedItem().equals("Creatures")) {
	                
	                for(GameObject creature :  ((Party) cave.searchByName(((String) searchBoxSub.getSelectedItem())).get(0)).getCreatures())
                    resultList.add(creature);
	                
	                for(String s : creatureSortOptions) {
	                    sortByBox.addItem(s);
	                }
	                
	                isSearchBoxSubReady = true;
	                sortByBox.setSelectedIndex(-1);
	                sortByBox.setEnabled(true);
	                
	            }
	            else if(searchBox.getSelectedItem().equals("Treasures")) {
	                
	                for(GameObject treasure : ((Creature) cave.searchByName(((String) searchBoxSub.getSelectedItem())).get(0)).getTreasures())
	                    resultList.add(treasure);
	                
	                for(String s : treasureSortOptions)
	                    sortByBox.addItem(s);
	                
	                isSearchBoxSubReady = true;
	                sortByBox.setSelectedIndex(-1);
	                sortByBox.setEnabled(true);
	            }
	            else if(searchBox.getSelectedItem().equals("Artifacts")) {
	            	for(GameObject artifact : ((Creature) cave.searchByName(((String) searchBoxSub.getSelectedItem())).get(0)).getArtifacts())
	                    resultList.add(artifact);
	                
	                for(String s : artifactSortOptions)
	                    sortByBox.addItem(s);
	                
	                isSearchBoxSubReady = true;
	                sortByBox.setSelectedIndex(-1);
	                sortByBox.setEnabled(true);
	            }
            }
        }
        else if(e.getActionCommand().equals("SortByBox")) {
            
            if(sortByBox.getSelectedIndex() != -1 && isSearchBoxSubReady) {
                if(sortByBox.getSelectedItem().equals("Index")) {
                    Collections.sort(resultList, new GameObjectIndexComparator());
                }
                else if(searchBox.getSelectedItem().equals("Creatures")) {
                    if(sortByBox.getSelectedItem().equals("Fear"))
                        Collections.sort(resultList, new CreatureFearComparator());
                    else if(sortByBox.getSelectedItem().equals("Empathy"))
                        Collections.sort(resultList, new CreatureEmpathyComparator());
                    else if(sortByBox.getSelectedItem().equals("Carry Capacity"))
                        Collections.sort(resultList, new CreatureCarryCapComparator());
                }
                else if(searchBox.getSelectedItem().equals("Treasures")) {
                    if(sortByBox.getSelectedItem().equals("Weight"))
                        Collections.sort(resultList, new TreasureWeightComparator());
                    else if(sortByBox.getSelectedItem().equals("Value"))
                        Collections.sort(resultList, new TreasureValueComparator());
                }
                else if(searchBox.getSelectedItem().equals("Artifacts")) {
                	if(sortByBox.getSelectedItem().equals("Type"))
                		Collections.sort(resultList, new ArtifactTypeComparator());
                }
                
                Result r = new Result();
                r.setResults(resultList);
                result = r;
                
                if(!result.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    
                    for(GameObject g : result.results)
                        sb.append(g.toString() + "\n");
                    
                    textArea.replaceRange(sb.toString(), 0, textArea.getDocument().getLength());
                    
                    // Update the view history stack
                    if(!viewIndexHistory.peek().equals(result)) {
                        viewIndexHistory.add(new Result(result));
                        gameTreeSurface.updateTreeView(result.results);
                    }
                }
                
                // Reset the alternate search
                resetSecondarySearch();
            }
        }
        // Handles setting the search type
        else if(e.getActionCommand().equals("idRadioButton")) {
            searchBy = SearchBy.id;
        }
        else if(e.getActionCommand().equals("nameRadioButton")) {
            searchBy = SearchBy.name;
        }
        else if(e.getActionCommand().equals("typeRadioButton")) {
            searchBy = SearchBy.type;
        }
    }
    
    // Reset the secondary search back to first drop-down selection
    private void resetSecondarySearch() {
    	// Reset the alternate search
        resultList.clear();
        searchBox.setSelectedIndex(-1);
        searchBoxSub.setSelectedIndex(-1);
        searchBoxSub.removeAllItems(); //reset the sub-options
        sortByBox.removeAllItems(); //reset sort options
        searchBoxSub.setEnabled(false);
        sortByBox.setEnabled(false);
        isSearchBoxReady = false;
        isSearchBoxSubReady = false;
    }

    // This method displaying each objects information when the corresponding button is pressed on
    //  the gameTreeSurface. A single click will only cause the button to be highlighted and
    //  display its information in the JTextArea in the IOSurface. A double click will reset
    //  the tree view with that node as the root, and also display its info. in the JTextArea.
    @Override
    public void mouseClicked(MouseEvent e) {
        JButton button = (JButton)e.getSource(); // only JButtons can be registered to this listener
        
        // Reset the secondary search method if anything was set before
        resetSecondarySearch();
        
        // Parse the user input 
        try {
            result.setResults(cave.searchByIndex(Integer.parseInt(button.getName())));
        } catch(InputMismatchException ex) {
            textArea.replaceRange("Must be an integer, try again.", 0, textArea.getDocument().getLength());
            return;
        } catch(NumberFormatException ex) {
            textArea.replaceRange("Please format your search correctly.", 0, textArea.getDocument().getLength());
            return;
        } catch(Exception ex) {
            textArea.replaceRange("Something went wrong, try again.", 0, textArea.getDocument().getLength());
            return;
        }
        
        if(!result.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            
            for(GameObject g : result.results)
                sb.append(g.toString() + "\n");
            
            textArea.replaceRange(sb.toString(), 0, textArea.getDocument().getLength());
            
            // Update the view history stack if the button is double clicked
            if(e.getClickCount() >= 2) {
                if(!viewIndexHistory.peek().equals(result)) {
                    viewIndexHistory.add(new Result(result));
                    gameTreeSurface.updateTreeView(result.results);
                }
            }
            // Highlight and display stats if only single clicked
            else {
                // Highlight the first selection
                if(currentSelection != null) {
                    
                    // Undo changes
                    currentSelection.setForeground(Color.BLACK);
                    
                    // Update selection
                    currentSelection = button;
                    
                    // Apply visual change
                    currentSelection.setForeground(Color.RED);
                } else {
                    
                    // Set current selection
                    currentSelection = button;
                    
                    // Apply visual changes
                    button.setForeground(Color.RED);
                }
            }
        }
    }

    // Unused methods from implementing MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    // Getters and setters
    /**
     * @param gameTreeSurface
     */
    public void setGameTreeSurface(GameTreeSurface gameTreeSurface) {
        this.gameTreeSurface = gameTreeSurface;
    }
    
    /**
     * @param result
     */
    public void setJTextArea(String result) {
        textArea.replaceRange(result, 0, textArea.getDocument().getLength());
    }

}
