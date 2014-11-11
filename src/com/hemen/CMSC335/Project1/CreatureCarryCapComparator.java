package com.hemen.CMSC335.Project1;

import java.util.Comparator;

public class CreatureCarryCapComparator implements Comparator<GameObject> {

    @Override
    public int compare(GameObject c1, GameObject c2) {
        if( ((Creature) c1).getCarryCapacity() > ((Creature) c2).getCarryCapacity() )
            return 1;
        else if( ((Creature) c1).getCarryCapacity() < ((Creature) c2).getCarryCapacity() )
            return -1;
        else
            return 0;
    }

}
