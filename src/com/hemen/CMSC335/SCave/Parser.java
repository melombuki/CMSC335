/*
 * Filename: Parser.java
 * Date: 1 Nov. 2014
 * Last Modified: 26 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: The Parser class contains the methods necessary for 
 *  creating aGUI representation from a predefined grammar. 
 *  Only the method file is public.
 */

package com.hemen.CMSC335.SCave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Parser {
    
    private final Cave cave;
    private final Lexer lexer;
    private Token token;
    private final HashMap<Integer, GameObject> hashMap;
    private final JobSurface jobSurface;
    
    //  The constructor establishes the input lexer and output JFrame object.
    public Parser(Lexer lexer, Cave cave, JobSurface jobSurface) {
       this.lexer = lexer;
       this.cave = cave;
       this.jobSurface = jobSurface;
       
       // Set the reference to the cave's hashMap
       hashMap = cave.getHashMap();
       
       // Add the cave itself to the hashMap
       hashMap.put(cave.index, cave);
    }
    
    //  file is the only public method. The guiStatement 
    //    is evaluated and the GUI is created.
    public void file() {
       token = lexer.getNextToken();
       while (token != Token.END_OF_FILE)
           parseLine();
    }
    
    // This method parses through a line from the input file
    private void parseLine() {
        switch (token) {
        case PARTY:
            parseParty();
            break;
        case CREATURE:
            parseCreature();
            break;
        case TREASURE:
            parseTreasure();
            break;
        case ARTIFACT:
            parseArtifact();
            break;
        case JOB:
            parseJob();
            break;
        default:
            System.out.println("Your line starting with \" " + lexer.getCurrentLexeme() + " is malformed. \n" +
                                "It was not added to the game state.");
            lexer.ignoreLineRemainder();
            token = lexer.getNextToken();
            break;
        }
    }
    
    // Parses a job
    private void parseJob() {
        int index = 0;
        String name = "";
        int creatureIndex = 0;
        double duration = 0;
        ArrayList<String> artifacts = new ArrayList<String>();
        ArrayList<Integer> amounts = new ArrayList<Integer>();
        
        verifyToken(Token.JOB); // j
        
        verifyToken(Token.INT); // <index>
        index = Integer.parseInt(lexer.getLastLexeme());
        
        verifyToken(Token.STRING); // <name>
        name = lexer.getLastLexeme();
        
        verifyToken(Token.INT); // <creature index>
        creatureIndex = Integer.parseInt(lexer.getLastLexeme());
        
        verifyToken(Token.DOUBLE); // <time>
        try {
            duration = Double.parseDouble(lexer.getLastLexeme());
        } catch (Exception e) {
            try {
                duration = Integer.parseInt(lexer.getLastLexeme());
            } catch (Exception ex) {
                System.out.println("Failed to parse duration for a job.");
                System.exit(-1);
            }
        }
        
        boolean isDone = false;
        while(!isDone) {
            if(lexer.peek() == Token.STRING) { // [<required artifact type>:<number>]*
                String artifactType = "";
                int requiredAmount = 0;
                
                verifyToken(Token.STRING); // <required artifact type>
                artifactType += lexer.getLastLexeme();
                
                verifyToken(Token.INT); // <number>
                requiredAmount = Integer.parseInt(lexer.getLastLexeme());
                
                // Add the pairs to the two array lists
                artifacts.add(artifactType);
                amounts.add(requiredAmount);
            } else {
                isDone = true;
            }
        }
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();
        
        // Instantiate the job, it runs itself
        ReentrantLock lock = ((Creature)hashMap.get(creatureIndex)).getLock();
        Condition condition = ((Creature)hashMap.get(creatureIndex)).getCanRunCondition();
        ConcurrentHashMap<String, ArrayList<Artifact>> resources = ((Creature)hashMap.get(creatureIndex)).getResources();
        Job job = new Job(index, name, creatureIndex, duration, artifacts,
                amounts, lock, condition, resources, jobSurface, cave);
        
        cave.add(job);
    }
    
    // Parses a party
    private void parseParty() {
        Party party = new Party();
        
        verifyToken(Token.PARTY); // p
        
        verifyToken(Token.INT); // <index>
        party.setIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.STRING); // <name>
        party.setName(lexer.getLastLexeme());
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();
        
        // Add the new party to the cave.
        //  Parties are always added to the cave.
        cave.add(party);
    }
    
    // Parses a creature
    private void parseCreature() {
        Creature creature = new Creature(cave);
        
        verifyToken(Token.CREATURE); // c
        
        verifyToken(Token.INT); // <index>
        creature.setIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.STRING); // <type>
        creature.setType(lexer.getLastLexeme());
        
        verifyToken(Token.STRING); // <name>
        creature.setName(lexer.getLastLexeme());
        
        verifyToken(Token.INT); // <party>
        creature.setParty(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.INT); // <empathy>
        creature.setEmpathy(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.INT); // <fear>
        creature.setFear(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.DOUBLE); // <carying capacity>
        try {
            creature.setCarryCapacity(Double.parseDouble(lexer.getLastLexeme()));
        } catch (Exception e) {
            try {
                creature.setCarryCapacity(Integer.parseInt(lexer.getLastLexeme()));
            } catch (Exception ex) {
                System.out.println("Failed to parse carry capacity for a creature");
                System.exit(-1);
            }
        }
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();
        
        cave.add(creature);
    }
    
    // Parses a treasure
    private void parseTreasure() {
        Treasure treasure = new Treasure();
        
        verifyToken(Token.TREASURE); // t

        verifyToken(Token.INT); // <index>
        treasure.setIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.STRING); // <type>
        treasure.setType(lexer.getLastLexeme());
        
        verifyToken(Token.INT); // <creature>
        treasure.setCreature(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.DOUBLE); // <weight>
        try {
            treasure.setWeight(Double.parseDouble(lexer.getLastLexeme()));
        } catch (Exception e) {
            try {
                treasure.setWeight(Integer.parseInt(lexer.getLastLexeme()));
            } catch (Exception ex) {
                System.out.println("Failed to parse a treasure");
                System.exit(-1);
            }
        }
        
        verifyToken(Token.INT); // <value>
        treasure.setValue(Integer.parseInt(lexer.getLastLexeme()));
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();
        
        cave.add(treasure);
    }
    
    // Parses an artifact
    private void parseArtifact() {
        Artifact artifact = new Artifact();
        
        verifyToken(Token.ARTIFACT); // a

        verifyToken(Token.INT); // <index>
        artifact.setIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.STRING); // <type>
        artifact.setType(lexer.getLastLexeme());
        
        verifyToken(Token.INT); // <creature>
        artifact.setCreature(Integer.parseInt(lexer.getLastLexeme()));
        
        if(lexer.peek() == Token.STRING) { // [<name>]
            verifyToken(Token.STRING);
            artifact.setName(lexer.getLastLexeme());
        }
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();

        cave.add(artifact);
    }
    
    // Verifies that the supplied token is the current token.
    // Displays an error message if it is not.
    private void verifyToken(Token requiredToken) {
        
        // Integers work for doubles, but doubles do not work for ints
        if(requiredToken == Token.DOUBLE && (token == Token.INT || token == Token.DOUBLE)) {
            token = lexer.getNextToken();
            return;
        }
        
        // Everything else must match exactly
        if (token != requiredToken) {
            // For now just print the error and quit
            System.out.println("Something was wrong with the " + requiredToken.name() + " " + lexer.getCurrentLexeme());
            System.exit(-1);
        }
        else {
            token = lexer.getNextToken();
        }
    }
}
