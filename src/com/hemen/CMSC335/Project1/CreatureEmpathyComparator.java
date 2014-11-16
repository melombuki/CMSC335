/*
 * Filename: CreatureEmpathyComparator.java
 * Date: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows creatures to be sorted
 *  by empathy.
 */

package com.hemen.CMSC335.Project1;

import java.util.Comparator;

public class CreatureEmpathyComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject c1, GameObject c2) {
        if( ((Creature) c1).getEmpathy() > ((Creature) c2).getEmpathy() )
            return 1;
        else if( ((Creature) c1).getEmpathy() < ((Creature) c2).getEmpathy() )
            return -1;
        else
            return 0;
    }

}
