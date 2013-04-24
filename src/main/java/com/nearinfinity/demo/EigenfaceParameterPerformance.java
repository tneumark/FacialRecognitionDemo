package com.nearinfinity.demo;

public class EigenfaceParameterPerformance implements Comparable {
    private final float correctMatchesPercentage;
    private final int numberOfComponents;
    private final float threshold;
    private final int kNearestNeighbors;

    public EigenfaceParameterPerformance(float correctMatchesPercentage, int numberOfComponents, float threshold, int kNearestNeighbors) {
        this.correctMatchesPercentage = correctMatchesPercentage;
        this.numberOfComponents = numberOfComponents;
        this.threshold = threshold;
        this.kNearestNeighbors = kNearestNeighbors;
    }

    @Override
    public String toString() {
        return "EigenfaceParameterPerformance{" +
                "correctMatchesPercentage=" + correctMatchesPercentage +
                ", numberOfComponents=" + numberOfComponents +
                ", threshold=" + threshold +
                ", kNearestNeighbors=" + kNearestNeighbors +
                '}';
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof EigenfaceParameterPerformance) {
            EigenfaceParameterPerformance other = (EigenfaceParameterPerformance)o;
            if (this.correctMatchesPercentage > other.correctMatchesPercentage) {
                return 1;
            }
            else if (this.correctMatchesPercentage < other.correctMatchesPercentage) {
                return -1;
            }
            return 0;
        }
        return -1;
    }
}
