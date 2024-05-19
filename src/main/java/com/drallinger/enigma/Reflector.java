package com.drallinger.enigma;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Reflector {
    private final int[][] wires;
    private final String name;
    private final boolean verbose;

    public Reflector(String fileName, boolean verbose){
        name = fileName;
        if(!fileName.endsWith(".json")){
            fileName = fileName + ".json";
        }
        Path reflectorFile = Paths.get(fileName);
        if(!Files.exists(reflectorFile)){
            System.err.printf("%s error: %s does not exist%n", EnigmaMachine.EXECUTABLE_NAME, reflectorFile);
            System.exit(1);
        }
        JSONObject object = null;
        try{
            object = new JSONObject(Files.readString(reflectorFile));
        }catch (IOException e){
            System.err.printf("%s error: %s%n", EnigmaMachine.EXECUTABLE_NAME, e.getMessage());
            System.exit(1);
        }
        wires = new int[13][2];
        JSONArray wiresArray = object.getJSONArray("wires");
        for(int i = 0; i < wiresArray.length(); i++){
            JSONArray innerArray = wiresArray.getJSONArray(i);
            wires[i][0] = innerArray.getInt(0);
            wires[i][1] = innerArray.getInt(1);
        }
        this.verbose = verbose;
    }

    public String swapLetters(String letter){
        int letterIndex = 0;
        for(var i = 0; i < EnigmaMachine.ALPHABET.length; i++){
            if(EnigmaMachine.ALPHABET[i].equals(letter)){
                letterIndex = i;
                break;
            }
        }
        int newIndex = 0;
        for(int[] wire : wires){
            if(letterIndex == wire[0]){
                newIndex = wire[1];
                break;
            }
            if(letterIndex == wire[1]){
                newIndex = wire[0];
                break;
            }
        }
        String newLetter = EnigmaMachine.ALPHABET[newIndex];
        if(verbose){
            System.out.printf("  %s: %s -> %s%n", name, letter, newLetter);
        }
        return newLetter;
    }
}
