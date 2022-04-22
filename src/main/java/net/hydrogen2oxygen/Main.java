package net.hydrogen2oxygen;

import org.apache.commons.cli.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Main method entry point. Start it once and see what options are available.
 */
public class Main {

    public static void main(String [] args) {

        Options options = new Options();
        options.addOption("url", true, "base url");
        options.addOption("parser", true, "parser implementation");
        options.addOption("output", true, "output folder");

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            String baseUrl = null;
            String parser = null;
            String output = null;
            boolean missingParam = false;

            if (cmd.hasOption("url")) {
                baseUrl = cmd.getOptionValue("url");
            } else {
                System.out.println("Missing option url!");
                missingParam = true;
            }

            if (cmd.hasOption("parser")) {
                parser = cmd.getOptionValue("parser");
            } else {
                System.out.println("Missing option parser!");
                missingParam = true;
            }

            if (cmd.hasOption("output")) {
                output = cmd.getOptionValue("output");
            } else {
                System.out.println("Missing option output!");
                missingParam = true;
            }

            if (missingParam) {
                showHelp(options);
                System.exit(0);
            }

            Class<?> clazz = Class.forName("net.hydrogen2oxygen.extractor.impl." + parser + "Extractor");
            Method method = clazz.getMethod("extract", String.class, String.class);
            Object obj = method.invoke(clazz.newInstance(), baseUrl, output);


        } catch (ParseException e) {
            System.err.println("Command parsing failed.  Reason: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Could not instantiate parser.  Reason: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.err.println("Could not instantiate extract method.  Reason: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Could not invoce class.  Reason: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Could not access method.  Reason: " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("Could not instantiate class.  Reason: " + e.getMessage());
        }
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("example", options);
    }
}
