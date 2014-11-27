/*
 * Filename: Cave.java
 * Date: 1 Nov. 2014
 * Last Modified: 24 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a cave in the game.
 *  The cave stores all other elements in the game.
 */

package com.hemen.CMSC335.SCave;

import java.util.ArrayList;
import java.util.HashMap;

public class Cave extends GameObject {
    
    private ArrayList<Party> parties; // parties in the cave
    private ArrayList<Creature> creatures; // creatures not in a party
    private ArrayList<Treasure> treasures; // treasures not carried
    private ArrayList<Artifact> artifacts; // artifacts not carried
    private HashMap<Integer, GameObject> hashMap; // all gameobjects by index
    private String name = "Cave";
    
    // Constructor
    public Cave() {
        parties = new ArrayList<Party>();
        treasures = new ArrayList<Treasure>();
        artifacts = new ArrayList<Artifact>();
        creatures = new ArrayList<Creature>();
        
        hashMap = new HashMap<Integer, GameObject>();
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Type: cave \n");
        
        return sb.toString();
    }
    
    // Searches the cave for a GameObject by its index
    //  It returns an ArrayList with the object or
    //  or an empty empty ArrayList if nothing is found.
    public ArrayList<GameObject> searchByIndex(int index) {
        ArrayList<GameObject> results = new ArrayList<GameObject>();
            
        GameObject g = hashMap.get(index);
        if(g != null)
            results.add(g);
        return results;
    }
    
    // Searches the cave for a GameObject by its name
    //  It returns a string with all of the objects that
    //  were found, or an empty string if nothing is found.
    public ArrayList<GameObject> searchByName(String name) {
        ArrayList<GameObject> results = new ArrayList<GameObject>();
        
        // Check the cave's Artifacts
        for(Artifact caveArtifact : artifacts) {
            if(caveArtifact.getName().equals(name))
                results.add(caveArtifact);
        }
        
        // Check the cave's Creatures
        for(Creature caveCreature : creatures) {
            if(caveCreature.getName().equals(name))
                results.add(caveCreature);
        }
        
        // Check the cave's parties
        for(Party party : this.parties) {
            if(party.getName().equals(name))
                results.add(party);
            
            // Check each party's creatures
            for(Creature creature : party.getCreatures()) {
                if(creature.getName().equals(name))
                    results.add(creature);
                
                // Check each creature's artifacts
                for(Artifact artifact : creature.getArtifacts()) {
                    if(artifact.getName().equals(name))
                        results.add(artifact);
                }
            }
        }
                
        return results;
    }

    // Searches the cave for a GameObject by its type
    //  It returns a string with each of the objects
    //  found, or an empty string if nothing is found.
    public ArrayList<GameObject> searchByType(String type) {
        ArrayList<GameObject> results = new ArrayList<GameObject>();

        // Check cave's Treasures
        for(Treasure caveTreasure : treasures) {
            if(caveTreasure.getType().equals(type))
                results.add(caveTreasure);
        }
        
        // Check the cave's Artifacts
        for(Artifact caveArtifact : artifacts) {
            if(caveArtifact.getType().equals(type))
                results.add(caveArtifact);
        }
        
        // Check the cave's Creatures
        for(Creature caveCreature : creatures) {
            if(caveCreature.getName().equals(name))
                results.add(caveCreature);
        }
        
        // Check the cave's party's children
        for(Party party : this.parties) {
            
            // Check each party's creatures
            for(Creature creature : party.getCreatures()) {
                if(creature.getType().equals(type))
                    results.add(creature);
                
                // Check each creature's treasures
                for(Treasure treasure : creature.getTreasures()) {
                    if(treasure.getType().equals(type))
                        results.add(treasure);
                }
                
                // Check each creature's artifacts
                for(Artifact artifact : creature.getArtifacts()) {
                    if(artifact.getType().equals(type))
                        results.add(artifact);
                }
            }
        }
                
        return results;
    }
    
    @Override
    public void add(Treasure treasure) {
        treasures.add(treasure);
    }
    
    @Override
    public void add(Artifact artifact) {
        artifacts.add(artifact);
    }
    
    @Override
    public void add(Party party) {
        parties.add(party);
    }
    
    // Getters and setters
    /**
     * @return the parties
     */
    public ArrayList<Party> getParties() {
        return parties;
    }
    
    /**
     * @return the treasures
     */
    public ArrayList<Treasure> getTreasures() {
        return treasures;
    }
    
    /**
     * @return the artifacts
     */
    public ArrayList<Artifact> getArtifacts() {
        return artifacts;
    }

    /**
     * @return the hashMap
     */
    public HashMap<Integer, GameObject> getHashMap() {
        return hashMap;
    }

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
}
