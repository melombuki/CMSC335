/*
 * Filename: TreasureWeightComparator.java
 * Date: 12 Nov. 2014
 * Last Modified: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows treasures to be sorted
 *  by weight.
 */

package com.hemen.CMSC335.SCave;

import java.util.Comparator;

public class TreasureWeightComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject t1, GameObject t2) {
        if( ((Treasure) t1).getWeight() > ((Treasure) t2).getWeight() )
            return 1;
        else if( ((Treasure) t1).getWeight() < ((Treasure) t2).getWeight() )
            return -1;
        else
            return 0;
    }

}
