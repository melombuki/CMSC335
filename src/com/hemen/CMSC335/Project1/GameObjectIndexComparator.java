package com.hemen.CMSC335.Project1;

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
