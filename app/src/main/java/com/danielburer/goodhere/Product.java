package com.danielburer.goodhere;

import android.support.annotation.NonNull;

public class Product implements Comparable<Product>{
    private String name;
    private String owner;
    private int pk;
    private int votes;
    private int score;

    public Product(String name, String owner, int pk, int votes, int score) {
        this.name = name;
        this.owner = owner;
        this.pk = pk;
        this.votes = votes;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getPk() {
        return pk;
    }

    public int getVotes() {
        return votes;
    }

    public int getScore() {
        return score;
    }

    public float getPercent() {
        try {
            return (this.score / (float)this.votes);
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Product o) {
        if (this.getPercent() < o.getPercent())
            return -1;
        else if (this.getPercent() > o.getPercent())
            return 1;
        return 0;
    }
}
