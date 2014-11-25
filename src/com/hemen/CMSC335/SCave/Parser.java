/*
 * The Parser class contains the methods necessary for creating a
 *  GUI representation from a predefined grammar. Only the file 
 *  method is public.
 */

package com.hemen.CMSC335.SCave;

import java.util.HashMap;

class Parser {
    
    private Cave cave;
    private Lexer lexer;
    private Token token;
    private HashMap<Integer, GameObject> hashMap;
    
    //  The constructor establishes the input lexer and output JFrame object.
    public Parser(Lexer lexer, Cave cave) {
       this.lexer = lexer;
       this.cave = cave;
       
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
        Job job = new Job();
        
        verifyToken(Token.JOB); // j
        
        verifyToken(Token.INT); // <index>
        job.setIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.STRING); // <name>
        job.setName(lexer.getLastLexeme());
        
        verifyToken(Token.INT); // <creature index>
        job.setCreatureIndex(Integer.parseInt(lexer.getLastLexeme()));
        
        verifyToken(Token.DOUBLE); // <time>
        try {
            job.setDuration(Double.parseDouble(lexer.getLastLexeme()));
        } catch (Exception e) {
            try {
                job.setDuration(Integer.parseInt(lexer.getLastLexeme()));
            } catch (Exception ex) {
                System.out.println("Failed to parse duration for a job.");
                System.exit(-1);
            }
        }
        
        boolean isDone = false;
        while(!isDone) {
            if(lexer.peek() == Token.STRING) { // [<required artifact type>:<number>]*
                String artifactName = "";
                int requiredAmount = 0;
                
                verifyToken(Token.STRING); // <required artifact type>
                artifactName += lexer.getLastLexeme();
                
                verifyToken(Token.INT); // <number>
                requiredAmount = Integer.parseInt(lexer.getLastLexeme());
                
                job.addRequirement(artifactName, requiredAmount);
            } else {
                isDone = true;
            }
        }
        
        // Ignore extra data
        lexer.ignoreLineRemainder();
        token = lexer.getNextToken();
        
        // Add the creature to the correct index
        hashMap.get(job.getCreatureIndex()).add(job);
        
        // Add the new creature to the hashMap
        hashMap.put(job.index, job);

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
        
        // Add the new party to the hashMap
        hashMap.put(party.index, party);
    }
    
    // Parses a creature
    private void parseCreature() {
        Creature creature = new Creature();
        
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
        
        // Add the creature to the correct index
        hashMap.get(creature.getParty()).add(creature);
        
        // Add the new creature to the hashMap
        hashMap.put(creature.index, creature);
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
        
        // Add the treasure to the correct index
        hashMap.get(treasure.getCreature()).add(treasure);
        
        // Add the new treasure to the hashMap
        hashMap.put(treasure.index, treasure);
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

        // Add the artifact to the correct index
        hashMap.get(artifact.getCreature()).add(artifact);
        
        // Add the new artifact to the hashMap
        hashMap.put(artifact.index, artifact);
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
