/*
 * Filename: CreatureFearComparator.java
 * Date: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows creatures to be sorted
 *  by fear.
 */

package com.hemen.CMSC335.SCave;

import java.util.Comparator;

public class CreatureFearComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject c1, GameObject c2) {
        if( ((Creature) c1).getFear() >  ((Creature) c2).getFear())
            return 1;
        else if( ((Creature) c1).getFear() <  ((Creature) c2).getFear())
            return -1;
        else
            return 0;
    }

}
