package com.hemen.CMSC335.Project1;

import java.util.Comparator;

public class ArtifactTypeComparator implements Comparator<GameObject> {

	 @Override
	    public int compare(GameObject a1, GameObject a2) {
	        return ((Artifact) a1).getType().compareTo(((Artifact) a2).getType());
	    }	    

}
