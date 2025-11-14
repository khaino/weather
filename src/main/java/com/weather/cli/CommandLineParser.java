package com.weather.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses command-line arguments for the weather application
 */
public class CommandLineParser {
    
    private boolean useFahrenheit = false;
    private List<String> locationArgs = new ArrayList<>();

    public CommandLineParser(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            if (arg.equals("-f") || arg.equals("--fahrenheit")) {
                useFahrenheit = true;
            } else if (!arg.startsWith("-")) {
                locationArgs.add(arg);
            }
        }
    }

    public boolean isUseFahrenheit() {
        return useFahrenheit;
    }

    public boolean hasLocationArgs() {
        return !locationArgs.isEmpty();
    }

    public String getLocationQuery() {
        return String.join(" ", locationArgs);
    }
}

