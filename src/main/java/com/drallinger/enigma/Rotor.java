package com.drallinger.enigma;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Rotor {
    public static final Path ROTORS_DIR = Paths.get("rotors");
    private final int notch;
    private final String name;
    private final boolean verbose;
    private int[] wires;
    private int currentIndex;

    public Rotor(String fileName, String startingPosition, boolean verbose){
        name = fileName;
        if(!fileName.endsWith(".json")){
            fileName = fileName + ".json";
        }
        Path rotorFile = Paths.get(ROTORS_DIR.toString(), fileName);
        if(!Files.exists(rotorFile)){
            System.err.printf("%s error: %s does not exist%n", EnigmaMachine.EXECUTABLE_NAME, fileName);
            System.exit(1);
        }
        JSONObject object = null;
        try{
            object = new JSONObject(Files.readString(rotorFile));
        }catch (IOException e){
            System.err.printf("%s error: %s%n", EnigmaMachine.EXECUTABLE_NAME, e.getMessage());
            System.exit(1);
        }
        notch = object.getInt("notch");
        wires = new int[26];
        JSONArray wiresArray = object.getJSONArray("wires");
        for(int i = 0; i < wiresArray.length(); i++){
            wires[i] = wiresArray.getInt(i);
        }
        currentIndex = 0;
        int startingIndex = 0;
        for(int i = 0; i < EnigmaMachine.ALPHABET.length; i++){
            String letter = EnigmaMachine.ALPHABET[i];
            if(letter.equals(startingPosition.toUpperCase())){
                startingIndex = i;
                break;
            }
        }
        this.verbose = verbose;
        spin(startingIndex);
    }

    public void spin(int amount){
        if(amount > 0){
            int[] temp = new int[26];
            for(int i = 0; i < wires.length; i++){
                int startingPoint = (i + amount) % 26;
                int endingPoint = (wires[i] + amount) % 26;
                temp[startingPoint] = endingPoint;
            }
            wires = temp;
            currentIndex = (currentIndex + amount) % 26;
            if(verbose){
                System.out.printf("  %s.spin: new position: %s%n", name, EnigmaMachine.ALPHABET[currentIndex]);
            }
        }
    }

    public String forwardCypher(String letter){
        String cypherLetter = "";
        for(int startingPoint = 0; startingPoint < wires.length; startingPoint++){
            String startingLetter = EnigmaMachine.ALPHABET[startingPoint];
            if(startingLetter.equals(letter)){
                int endingPoint = wires[startingPoint];
                cypherLetter = EnigmaMachine.ALPHABET[endingPoint];
                break;
            }
        }
        if(verbose){
            System.out.printf("  %s.forwardCypher: %s -> %s%n", name, letter, cypherLetter);
        }
        return cypherLetter;
    }

    public String reverseCypher(String letter){
        String cypherLetter = "";
        for(int startingPoint = 0; startingPoint < wires.length; startingPoint++){
            int endingPoint = wires[startingPoint];
            String endingLetter = EnigmaMachine.ALPHABET[endingPoint];
            if(endingLetter.equals(letter)){
                cypherLetter = EnigmaMachine.ALPHABET[startingPoint];
                break;
            }
        }
        if(verbose){
            System.out.printf("  %s.reverseCypher: %s -> %s%n", name, letter, cypherLetter);
        }
        return cypherLetter;
    }

    public boolean isAtNotch(){
        return notch == currentIndex;
    }
}
