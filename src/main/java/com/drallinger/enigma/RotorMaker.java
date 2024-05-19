package com.drallinger.enigma;

import org.json.JSONWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class RotorMaker {
    private static final int MAX_RETRIES = 5;
    private final Random random;

    public RotorMaker(){
        random = new Random();
    }

    public void createRotorFile(String fileName){
        if(!Files.exists(Rotor.ROTORS_DIR)){
            try{
                Files.createDirectory(Rotor.ROTORS_DIR);
            }catch (IOException e){
                System.err.printf("%s error: %s%n", EnigmaMachine.EXECUTABLE_NAME, e.getMessage());
                System.exit(1);
            }
        }
        if(!fileName.endsWith(".json")){
            fileName = fileName + ".json";
        }
        Path newFile = Paths.get(Rotor.ROTORS_DIR.toString(), fileName);
        if(Files.exists(newFile)){
            System.err.printf("%s error: %s already exists%n", EnigmaMachine.EXECUTABLE_NAME, fileName);
            System.exit(1);
        }
        int[] wires = createRotorWires();
        int notch = random.nextInt(26);
        try(BufferedWriter writer = Files.newBufferedWriter(newFile)){
            JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter
                .object()
                .key("notch")
                .value(notch)
                .key("wires")
                .array();
            for(int wire : wires){
                jsonWriter.value(wire);
            }
            jsonWriter
                .endArray()
                .endObject();
        }catch (IOException e){
            System.err.printf("%s error: %s%n", EnigmaMachine.EXECUTABLE_NAME, e.getMessage());
            System.exit(1);
        }
        System.out.printf("Created new rotor: %s%n", fileName);
    }

    private int[] createRotorWires(){
        int[] wires = new int[26];
        ArrayList<Integer> availableNumbers = new ArrayList<>(IntStream.range(0, 26).boxed().toList());
        for(int i = 0; i < 26; i++){
            int randomIndex = random.nextInt(availableNumbers.size());
            int randomNumber = availableNumbers.get(randomIndex);
            int retries = 0;
            while(randomNumber == i){
                randomIndex = random.nextInt(availableNumbers.size());
                randomNumber = availableNumbers.get(randomIndex);
                if(++retries >= MAX_RETRIES){
                    System.err.printf("%s error: failed to create rotor due to bad luck during random number selection, please try again%n", EnigmaMachine.EXECUTABLE_NAME);
                    System.exit(1);
                }
            }
            wires[i] = randomNumber;
            availableNumbers.remove(randomIndex);
        }
        return wires;
    }
}
