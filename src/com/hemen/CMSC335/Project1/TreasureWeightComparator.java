package com.hemen.CMSC335.Project1;

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
