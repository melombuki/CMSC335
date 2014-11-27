/*
 * Filename: ArtifactTypeComparator.java
 * Date: 12 Nov. 2014
 * Last Modified: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows artifacts to be sorted
 *  by type.
 */

package com.hemen.CMSC335.SCave;

import java.util.Comparator;

public class ArtifactTypeComparator implements Comparator<GameObject> {

	 @Override
	    public int compare(GameObject a1, GameObject a2) {
	        return ((Artifact) a1).getType().compareTo(((Artifact) a2).getType());
	    }	    

}
