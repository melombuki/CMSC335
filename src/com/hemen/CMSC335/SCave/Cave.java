/*
 * Filename: Cave.java
 * Date: 1 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a cave in the game.
 *  The cave stores all other elements in the game.
 */

package com.hemen.CMSC335.SCave;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Cave extends GameObject {
    
    private final ArrayList<Party> parties; // parties in the cave
    private final ArrayList<Creature> creatures; // creatures not in a party
    private final ArrayList<Treasure> treasures; // treasures not carried
    private final ArrayList<Artifact> artifacts; // artifacts not carried
    private final ConcurrentHashMap<Integer, GameObject> resourcePool;
    private final ConcurrentHashMap<Integer, GameObject> hashMap; // all GameObjects by index
    private String name = "Cave";
    private static final String REMOVEJTREECOMPONENT = "RemoveJTreeComponent";
    private static final String ADDJTREECOMPONENT = "AddJTreeComponent";
    private final ActionListener listener;
    
    // Constructor
    public Cave(ActionListener a) {
        super();
        
        parties = new ArrayList<Party>();
        treasures = new ArrayList<Treasure>();
        artifacts = new ArrayList<Artifact>();
        creatures = new ArrayList<Creature>();
        listener = a;
        
        parentIndex = -1; //it has no parent
        
        hashMap = new ConcurrentHashMap<Integer, GameObject>();
        resourcePool = new ConcurrentHashMap<Integer, GameObject>();
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        
        sb.append("Index: " + index + "\n" +
                  "Type: cave \n");
        
        return sb.toString();
    }
    
    // Searches the cave for a GameObject by its index
    //  as a String. It returns an ArrayList with the object or
    //  or an empty empty ArrayList if nothing is found.
    public GameObject searchByIndex(int index) {        
        return hashMap.get(index);
    }
    
    // Searches the cave for a GameObject by its name
    //  It returns a string with all of the objects that
    //  were found, or an empty string if nothing is found.
    public ArrayList<GameObject> searchByName(String name) {
        ArrayList<GameObject> results = new ArrayList<GameObject>();
        
        if(this.name.equals(name))
            results.add(this);
        
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
        
        // Check the cave's Treasures
        for(Treasure caveTreasure : treasures) {
            if(caveTreasure.getName().equals(name))
                results.add(caveTreasure);
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
                
                // Check each creature's jobs
                for(Job job : creature.getJobs()) {
                    if(job.getName().equals(name))
                        results.add(job);
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
        
        // Check cave's Treasures
        for(Treasure caveTreasure : treasures) {
            if(caveTreasure.getType().equals(type))
                results.add(caveTreasure);
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
    
    // This method adds a creature to the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
	@Override
    public void add(Creature creature) {
    	// Add the creature to the correct index
    	if(creature.getParent() == this.index) {
    		creatures.add(creature);
    	} else {
    		hashMap.get(creature.getParent()).add(creature);
    	}
        
        // Add the new creature to the hashMap
        hashMap.put(creature.index, creature);
        
        listener.actionPerformed(new ActionEvent(creature, ActionEvent.ACTION_PERFORMED, ADDJTREECOMPONENT) {});
    }
    
    // This method adds a treasure to the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
	@Override
    public void add(Treasure treasure) {
        // Add the treasure to the correct index
    	if(treasure.getParent() == this.index) {
    		treasures.add(treasure);
    	} else {
    		hashMap.get(treasure.getParent()).add(treasure);
    	}
        
        // Add the new treasure to the hashMap
        hashMap.put(treasure.index, treasure);
        resourcePool.put(treasure.index, treasure);
        
        listener.actionPerformed(new ActionEvent(treasure, ActionEvent.ACTION_PERFORMED, ADDJTREECOMPONENT) {});
    }
    
    // This method adds an artifact to the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
	@Override
    public void add(Artifact artifact) {
        // Add the artifact to the correct index
    	if(artifact.getParent() == this.index) {
    		artifacts.add(artifact);
    	} else {
    		hashMap.get(artifact.getParent()).add(artifact);
    	}
        
        // Add the new artifact to the hashMap
        hashMap.put(artifact.index, artifact);
        resourcePool.put(artifact.index, artifact);
        
        listener.actionPerformed(new ActionEvent(artifact, ActionEvent.ACTION_PERFORMED, ADDJTREECOMPONENT) {});
    }
    
    // This method adds a party to the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
    @Override
    public void add(Party party) {
        parties.add(party);
        
        // Add the new party to the hashMap
        hashMap.put(party.index, party);
        
        listener.actionPerformed(new ActionEvent(party, ActionEvent.ACTION_PERFORMED, ADDJTREECOMPONENT) {});
    }
    
    // This method adds a job to the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
    @Override
    public void add(Job job) {
        // Add the creature to the correct index
        hashMap.get(job.getParent()).add(job);
        
        // Add the new creature to the hashMap
        hashMap.put(job.index, job);
        
        listener.actionPerformed(new ActionEvent(job, ActionEvent.ACTION_PERFORMED, ADDJTREECOMPONENT) {});
    }
    
    // This method removes an artifact from the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
	public void remove(Artifact artifact) {
    	// Remove the job from it's owner
    	hashMap.get(artifact.getParent()).getArtifacts().remove(artifact);
    	
    	// Remove the creature to the hashMap
        hashMap.remove(artifact.index);
        
        listener.actionPerformed(new ActionEvent(artifact, ActionEvent.ACTION_PERFORMED, REMOVEJTREECOMPONENT) {});
    }
    
    // This method removes a treasure from the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
    public void remove(Treasure treasure) {
        // Remove the job from it's owner
        hashMap.get(treasure.getParent()).getArtifacts().remove(treasure);
        
        // Remove the creature to the hashMap
        hashMap.remove(treasure.index);
        
        listener.actionPerformed(new ActionEvent(treasure, ActionEvent.ACTION_PERFORMED, REMOVEJTREECOMPONENT) {});
    }
    
    // This method removes a creature from the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
    public void remove(Creature creature) {
        // Remove the job from it's owner
        hashMap.get(creature.getParent()).getCreatures().remove(creature);
        
        // Remove the creature to the hashMap
        hashMap.remove(creature.index);
        
        listener.actionPerformed(new ActionEvent(creature, ActionEvent.ACTION_PERFORMED, REMOVEJTREECOMPONENT) {});
    }
    
    // This method removes a job from the game. It also updates the JTree through
    //  sending a message to the listener.
    @SuppressWarnings("serial")
	public void remove(Job job) {
    	// Remove the job from it's owner
    	((Creature)hashMap.get(job.getParent())).getJobs().remove(job);
    	
    	// Remove the creature to the hashMap
        hashMap.remove(job.index);
        
        listener.actionPerformed(new ActionEvent(job, ActionEvent.ACTION_PERFORMED, REMOVEJTREECOMPONENT) {});
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
     * @return the creatures
     */
    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    /**
     * @return the hashMap
     */
    public ConcurrentHashMap<Integer, GameObject> getHashMap() {
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
