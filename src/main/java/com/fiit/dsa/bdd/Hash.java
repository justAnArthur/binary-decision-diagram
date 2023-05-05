package com.fiit.dsa.bdd;

public class Hash {
    public static int hash(String key, int tableSize) {
        long sum = 0;

        for (int i = 0; i < key.length(); i++)
            sum += ((int) key.charAt(i)) * (sum + 1) + 31L * i;

        return Math.abs((int) (23 * sum + 197) % tableSize);
    }
}


