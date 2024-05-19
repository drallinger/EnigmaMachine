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

public class ReflectorMaker {
    private final Random random;

    public ReflectorMaker(){
        random = new Random();
    }

    public void createReflectorFile(String fileName) {
        if(!fileName.endsWith(".json")){
            fileName = fileName + ".json";
        }
        Path newFile = Paths.get(fileName);
        if(Files.exists(newFile)){
            System.err.printf("%s error: %s already exists%n", EnigmaMachine.EXECUTABLE_NAME, fileName);
            System.exit(1);
        }
        int[][] wires = createReflectorWires();
        try(BufferedWriter writer = Files.newBufferedWriter(newFile)){
            JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter
                .object()
                .key("wires")
                .array();
            for(int[] wire : wires){
                jsonWriter
                    .array()
                    .value(wire[0])
                    .value(wire[1])
                    .endArray();
            }
            jsonWriter
                .endArray()
                .endObject();
        }catch (IOException e){
            System.err.printf("%s error: %s%n", EnigmaMachine.EXECUTABLE_NAME, e.getMessage());
            System.exit(1);
        }
        System.out.printf("Created new reflector: %s%n", fileName);
    }

    private int[][] createReflectorWires(){
        int[][] wires = new int[13][2];
        ArrayList<Integer> availableNumbers = new ArrayList<>(IntStream.range(0, 26).boxed().toList());
        for(int i = 0; i < 13; i++){
            int index1 = random.nextInt(availableNumbers.size());
            wires[i][0] = availableNumbers.get(index1);
            availableNumbers.remove(index1);

            int index2 = random.nextInt(availableNumbers.size());
            wires[i][1] = availableNumbers.get(index2);
            availableNumbers.remove(index2);
        }
        return wires;
    }
}
