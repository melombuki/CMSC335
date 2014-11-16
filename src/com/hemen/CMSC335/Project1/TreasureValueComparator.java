/*
 * Filename: TreasureValueComparator.java
 * Date: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows treasures to be sorted
 *  by value.
 */

package com.hemen.CMSC335.Project1;

import java.util.Comparator;

public class TreasureValueComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject t1, GameObject t2) {
        if( ((Treasure) t1).getValue() > ((Treasure) t2).getValue() )
            return 1;
        else if( ((Treasure) t1).getValue() < ((Treasure) t2).getValue() )
            return -1;
        else
            return 0;
    }
    
}
