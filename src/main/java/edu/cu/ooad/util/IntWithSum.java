package edu.cu.ooad.util;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class IntWithSum {
    /**
     * Return list of 'numOfInt' non-negative (psuedo)random integers which sum to 'sumOfInt',
     * returns null if invalid arguments supplied
    */
    public static List<Integer> getIntegersWithSum(Integer numOfInt, Integer sumOfInt) {
        if (numOfInt < 1 || sumOfInt < 0) {
            return null;
        }

        List<Double> doubles = new LinkedList<>();
        for(int i=0; i<numOfInt; i++) {
            doubles.add(Math.random());
        }
        Double sumOfDouble = doubles.stream()
                .mapToDouble(d -> d)
                .sum();
        List<Integer> integers = new ArrayList<>(numOfInt);
        integers = doubles.stream()
                .map(d -> {
                    return scale(d, sumOfDouble, sumOfInt);
                })
                .collect(Collectors.toList());

        Integer actualSumOfInt = integers.stream()
                .mapToInt(i -> i)
                .sum();

        //really ensures that sum of int is as required
        if (actualSumOfInt == sumOfInt) {
            return integers;
        }
        else {
            //Unit integer (with magnitude 1), sign indicates should add or remove value from given int
            Integer unit = (sumOfInt-actualSumOfInt)/Math.abs(sumOfInt-actualSumOfInt);

            //Update element in list randomly till required sum is achieved
            while (sumOfInt != actualSumOfInt) {
                int i = new Random().nextInt(integers.size());
                integers.set(i, integers.get(i) + unit);
                actualSumOfInt += unit;
            }
        }
        return integers;
    }

    /**
     * @param d Double
     * @param sumOfDouble Scale the current value in
     * @param sumOfInt Scale to this value
     * @return Integer, sacles the double and convert it to Integer
     */
    private static Integer scale(Double d, Double sumOfDouble, Integer sumOfInt) {
        Double nd = (d / sumOfDouble) * sumOfInt;
        return nd.intValue();
    }
}
