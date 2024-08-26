package com.helpers.flywayhelper.entities

class MigrationVersionComparator : Comparator<MigrationVersion> {
    override fun compare(o1: MigrationVersion?, o2: MigrationVersion?): Int {
        if (o1 == null) {
            return -1
        }
        if (o2 == null) {
            return 1
        }

        //0 if version parts equal; 1 if first > second; 2 if second > first;
        val comparisonResult = o1.getMinimalVersionParts()!!.mapIndexed { index, p ->
            val p2 = o2.getMinimalVersionParts()!![index]
            var result = 0
            if (p > p2) {
                result = 1
            }
            if (p < p2) {
                result = 2
            }
            result
        }
        val o1Index = comparisonResult.indexOfFirst { p -> p == 1 } // index when o1 part is superior to o2 part
        val o2Index = comparisonResult.indexOfFirst { p -> p == 2 } // index when o2 part is superior to o1 part

        return if (o1Index == -1 && o2Index == -1) {
            0
        } else if (o1Index == -1) {
            -1
        } else if (o2Index == -1) {
            1
        } else if (o1Index < o2Index) {
            1
        } else {
            -1
        }
    }
}