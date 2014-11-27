/*
 * Filename: GameObjectIndexComparator.java
 * Date: 12 Nov. 2014
 * Last Modified: 12 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class allows game objects to be sorted
 *  by index.
 */

package com.hemen.CMSC335.SCave;

import java.util.Comparator;

public class GameObjectIndexComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject o1, GameObject o2) {
        if(o1.getIndex() > o2.getIndex() )
            return 1;
        else if( o1.getIndex() < o2.getIndex() )
            return -1;
        else
            return 0;
    }

}
