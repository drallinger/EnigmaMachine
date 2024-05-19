package com.drallinger.enigma;

import org.apache.commons.cli.*;

public class EnigmaMachine {
    public static final String[] ALPHABET = {
        "A", "B", "C", "D",
        "E", "F", "G", "H",
        "I", "J", "K", "L",
        "M", "N", "O", "P",
        "Q", "R", "S", "T",
        "U", "V", "W", "X",
        "Y", "Z"
    };
    public static final String EXECUTABLE_NAME = "enigma.sh";
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        Options options = getOptions();
        CommandLine cmd = getCommandLine(options, args);

        if(cmd.hasOption("h") || args.length == 0) {
            help(options);
            return;
        }else if(cmd.hasOption("V")){
            version();
            return;
        }else if(cmd.hasOption("R")){
            createRotor(cmd.getOptionValue("R"));
            return;
        } else if(cmd.hasOption("F")){
            createReflector(cmd.getOptionValue("F"));
            return;
        }

        if(!cmd.hasOption("r")){
            System.err.printf("%s error: no rotors given%n", EXECUTABLE_NAME);
            System.exit(1);
        }
        if(!cmd.hasOption("f")){
            System.err.printf("%s error: no reflector given%n", EXECUTABLE_NAME);
            System.exit(1);
        }
        if(!cmd.hasOption("i")){
            System.err.printf("%s error: no input message given%n", EXECUTABLE_NAME);
            System.exit(1);
        }

        boolean verbose = cmd.hasOption("v");
        Rotor[] rotors = getRotors(cmd.getOptionValues("r"), verbose);
        Reflector reflector = new Reflector(cmd.getOptionValue("f"), verbose);
        if(verbose){
            System.out.println("enigma: setting up plugboard");
        }
        Plugboard plugboard = new Plugboard(cmd.hasOption("p") ? cmd.getOptionValues("p") : new String[0], verbose);
        String inputMessage = cmd.getOptionValue("i").toUpperCase().replaceAll("[^A-Z]", "");

        runCypher(rotors, reflector, plugboard, inputMessage, verbose);
    }

    private static Options getOptions(){
        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("V", "version", false, "print the version number");
        Option verbose = new Option("v", "verbose", false, "be extra verbose");
        Option createRotor = Option.builder("R")
            .longOpt("create-rotor")
            .hasArg()
            .argName("rotor filename")
            .desc("create a rotor file with the given name")
            .build();
        Option createReflector = Option.builder("F")
            .longOpt("create-reflector")
            .hasArg()
            .argName("reflector filename")
            .desc("create a reflector file with the given name")
            .build();
        Option rotor = Option.builder("r")
            .longOpt("rotor")
            .hasArgs()
            .argName("rotor:position")
            .desc("the rotor name and position (A-Z)")
            .build();
        Option reflector = Option.builder("f")
            .longOpt("reflector")
            .hasArg()
            .argName("reflector")
            .desc("the reflector name")
            .build();
        Option input = Option.builder("i")
            .longOpt("input")
            .hasArg()
            .argName("message")
            .desc("the string to encrypt/decrypt")
            .build();
        Option plugboard = Option.builder("p")
            .longOpt("plugboard")
            .hasArgs()
            .argName("letter:letter")
            .desc("the two letters to swap")
            .build();

        Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(verbose);
        options.addOption(createRotor);
        options.addOption(createReflector);
        options.addOption(rotor);
        options.addOption(reflector);
        options.addOption(input);
        options.addOption(plugboard);
        return options;
    }

    private static CommandLine getCommandLine(Options options, String[] args){
        CommandLine cmd = null;
        CommandLineParser parser = new DefaultParser(false);
        try{
            cmd = parser.parse(options, args);
        }catch (ParseException e){
            System.err.printf("%s error: %s%n", EXECUTABLE_NAME, e.getMessage());
            System.exit(1);
        }
        return cmd;
    }

    private static void help(Options options){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(EXECUTABLE_NAME + " [options]", options);
    }

    private static void version(){
        System.out.printf("%s %s%n", EXECUTABLE_NAME, VERSION);
    }

    private static void createRotor(String fileName){
        RotorMaker rotorMaker = new RotorMaker();
        rotorMaker.createRotorFile(fileName);
    }

    private static void createReflector(String fileName){
        ReflectorMaker reflectorMaker = new ReflectorMaker();
        reflectorMaker.createReflectorFile(fileName);
    }

    private static Rotor[] getRotors(String[] rotorOptions, boolean verbose){
        if(verbose){
            System.out.println("enigma: setting up rotors");
        }
        Rotor[] rotors = new Rotor[rotorOptions.length];
        for(int i = 0; i < rotorOptions.length; i++){
            String rotorOption = rotorOptions[i];
            if(!rotorOption.contains(":")){
                System.err.printf("%s error: invalid rotor: %s%n", EXECUTABLE_NAME, rotorOption);
                System.exit(1);
            }
            String[] rotorOptionsArray = rotorOption.split(":");
            rotors[i] = new Rotor(rotorOptionsArray[0], rotorOptionsArray[1], verbose);
        }
        return rotors;
    }

    private static void runCypher(Rotor[] rotors, Reflector reflector, Plugboard plugboard, String inputMessage, boolean verbose){
        if(verbose){
            System.out.printf("enigma: running cypher for message \"%s\"%n", inputMessage);
        }
        StringBuilder output = new StringBuilder();
        String[] inputArray = inputMessage.split("");
        for(String letter : inputArray){
            if(verbose){
                System.out.printf("enigma: starting letter: %s%n", letter);
            }

            letter = plugboard.swapLetters(letter);

            boolean spinNextRotor = false;
            for(int i = 0; i < rotors.length; i++){
                Rotor rotor = rotors[i];
                if(i == 0 || spinNextRotor){
                    spinNextRotor = rotor.isAtNotch();
                    rotor.spin(1);
                }
                letter = rotor.forwardCypher(letter);
            }

            letter = reflector.swapLetters(letter);

            for(int i = rotors.length - 1; i >= 0; i--){
                Rotor rotor = rotors[i];
                letter = rotor.reverseCypher(letter);
            }

            letter = plugboard.swapLetters(letter);

            if(verbose){
                System.out.printf("enigma: ending letter: %s%n", letter);
            }
            output.append(letter);
        }
        System.out.println(output);
    }
}
