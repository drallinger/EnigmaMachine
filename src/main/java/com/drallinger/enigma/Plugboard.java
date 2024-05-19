package com.drallinger.enigma;

import java.util.ArrayList;
import java.util.HashSet;

public class Plugboard {
    private final String[][] wires;
    private final boolean verbose;

    public Plugboard(String[] optionValues, boolean verbose){
        if(optionValues.length > 10){
            System.err.printf("%s error: too many plugboard options given: %d (max 10)%n", EnigmaMachine.EXECUTABLE_NAME, optionValues.length);
            System.exit(1);
        }
        wires = new String[optionValues.length][2];
        HashSet<String> existingLetters = new HashSet<>();
        this.verbose = verbose;
        for(int i = 0; i < optionValues.length; i++){
            String optionValue = optionValues[i];
            if(!optionValue.contains(":")){
                System.err.printf("%s error: invalid plugboard option: %s%n", EnigmaMachine.EXECUTABLE_NAME, optionValue);
                System.exit(1);
            }
            String[] optionValueArray = optionValue.split(":");
            for(int j = 0; j < optionValueArray.length; j++){
                String letter = optionValueArray[j].toUpperCase();
                if(existingLetters.contains(letter)){
                    System.err.printf("%s error: letter already set in plugboard: %s%n", EnigmaMachine.EXECUTABLE_NAME, letter);
                    System.exit(1);
                }
                wires[i][j] = letter;
                existingLetters.add(letter);
            }
            if(verbose){
                System.out.printf("  plugboard: added letter pair: %s%n", optionValue);
            }
        }
    }

    public String swapLetters(String letter){
        String newLetter = letter;
        for(String[] wire : wires){
            if(wire[0].equals(letter)){
                newLetter = wire[1];
                break;
            }
            if(wire[1].equals(letter)){
                newLetter = wire[0];
                break;
            }
        }
        if(verbose && !letter.equals(newLetter)){
            System.out.printf("  plugboard: %s -> %s%n", letter, newLetter);
        }
        return newLetter;
    }
}
