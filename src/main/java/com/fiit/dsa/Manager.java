package com.fiit.dsa;

import com.fiit.dsa.bdd.BDD;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.fiit.dsa.bdd.Expression.*;

public class Manager {

    private static final Scanner scanner = new Scanner(System.in);

    private static Integer handleNumber(String text) {
        System.out.println(text + " `number` :");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception ex) {
            return handleNumber(text);
        }
    }

    private static String handleInput(String text, String[] acceptableStringedOptions) {
        System.out.println(text + " [" + Arrays.stream(acceptableStringedOptions).filter(string -> string.charAt(0) != '_').collect(Collectors.joining(", ")) + "]:");

        String line = scanner.nextLine();

        if (line.isEmpty())
            handleInput(text, acceptableStringedOptions);

        return Arrays.stream(acceptableStringedOptions)
                .filter(string -> string.toLowerCase().contains(line.toLowerCase()))
                .findFirst()
                .orElseGet(() -> handleInput(text, acceptableStringedOptions));
    }

    private static String handleInput(String text, Function<String, Boolean> acceptableStringedOptions) {
        System.out.println(text);

        String line = scanner.nextLine();

        if (line.isEmpty() || !acceptableStringedOptions.apply(line))
            handleInput(text, acceptableStringedOptions);

        return line;
    }

    public static void main(String[] args) {

        final String methodOfTesting = handleInput("Method of testing:", new String[]{"automatic", "manual", "_logger", "exit"});

        try {
            switch (methodOfTesting) {
                case "automatic" -> runAutomatic(
                        handleNumber("Size of tested expressions"),
                        handleNumber("Number of variables"),
                        handleInput("With best order?", new String[]{"yes", "no"}).equals("yes"),
                        false
                );
                case "manual" -> runManual();
                case "_logger" -> runLogger();
                case "exit" -> System.exit(0);
            }
        } catch (Exception ex) {
            System.out.println("~ There was an error: " + ex.getMessage());
        }

        main(args);
    }

    private static void runManual() {
        final String expression = handleInput("Expression (in lowercase is inverted): ", (string) -> {
            try {
                return string.matches("[a-zA-Z+]*");
            } catch (Exception ex) {
                return false;
            }
        });
        String order = handleInput("Order: ", (string) -> {
            try {
                return !validateVarTypes(expression, string) || string.equals("BEST");
            } catch (Exception ex) {
                return false;
            }
        });

        BDD bdd;
        if (order.equals("BEST"))
            bdd = new BDD().createWithBestOrder(expression);
        else
            bdd = new BDD().create(expression, order);

        int length = (int) Math.pow(2, order.length());

        String[] bddOutput = new String[length];
        for (int i = 0; i < length; i++) {
            bddOutput[i] = bdd.use(String.format("%" + order.length() + "s", Integer.toBinaryString(i)).replace(' ', '0'));
        }

        String[] evaluatedOutput = new String[length];
        for (int i = 0; i < length; i++) {
            evaluatedOutput[i] = evaluateExpression(expression, String.format("%" + order.length() + "s", Integer.toBinaryString(i)).replace(' ', '0'));
        }

        System.out.println("Vector | BDD | Evaluated");
        for (int i = 0; i < length; i++) {
            System.out.print(String.format("%" + order.length() + "s", Integer.toBinaryString(i)).replace(' ', '0'));
            System.out.print(" | ");
            System.out.print(evaluatedOutput[i]);
            System.out.print(" | ");
            System.out.println(evaluatedOutput[i]);
        }

        System.out.println("Percentage of reduction: " + (100 - (bdd.getNumberOfNodesAfterReduction() / bdd.getNumberOfNodes()) * 100) + " %");
    }

    private static String[] runAutomatic(Integer size, Integer numberOfVariables, boolean isBestOrder, boolean isReturning) {

        // Generate `size` expressions
        String[][] expressions = new String[size][2];
        for (int i = 0; i < size; i++)
            expressions[i] = generateExpression(numberOfVariables);


        // Generate `size` bdd and timer a time on generation and writing to storage
        List<BDD> bddList = new ArrayList<>(size);
        long timeOnCreating;

        if (isBestOrder) {
            timeOnCreating = System.nanoTime();

            for (int i = 0; i < size; i++)
                bddList.add(new BDD().createWithBestOrder(expressions[i][1]));

            timeOnCreating = System.nanoTime() - timeOnCreating;
        } else {
            timeOnCreating = System.nanoTime();

            for (int i = 0; i < size; i++)
                bddList.add(new BDD().create(expressions[i][1], expressions[i][0]));

            timeOnCreating = System.nanoTime() - timeOnCreating;
        }


        // Time of using a bdd
        String[] vectors = new String[(int) Math.pow(2, numberOfVariables)];
        for (int i = 0; i < Math.pow(2, numberOfVariables); i++)
            vectors[i] = String.format("%" + numberOfVariables + "s", Integer.toBinaryString(i)).replace(' ', '0');

        List<String[]> bddOutputs = new ArrayList<>(size);
        long timeOnUsing = System.nanoTime();

        for (int i = 0; i < size; i++) {
            int length = expressions[i][0].length();
            String[] output = new String[length];

            for (int j = 0; j < length; j++)
                output[j] = bddList.get(i).use(vectors[j]);

            bddOutputs.add(output);
        }

        timeOnUsing = System.nanoTime() - timeOnUsing;


        // Time of evaluating an expression
        List<String[]> evaluatedOutputs = new ArrayList<>(size);
        long timeOnEvaluating = System.nanoTime();

        for (int i = 0; i < size; i++) {
            int length = expressions[i][0].length();
            String[] output = new String[length];

            for (int j = 0; j < length; j++)
                output[j] = evaluateExpression(expressions[i][1], vectors[j]);

            evaluatedOutputs.add(output);
        }

        timeOnEvaluating = System.nanoTime() - timeOnEvaluating;


        if (!isReturning) {
            // Printing results
            System.out.println("Time on creating: " + (double) timeOnCreating / 1000000.0 + " ms");
            System.out.println("Time on using: " + (double) timeOnUsing / 1000000.0 + " ms");
            System.out.println("Time on evaluating: " + (double) timeOnEvaluating / 1000000.0 + " ms");
            System.out.println("Percentage of reduction: "
                    + (100 - ((bddList.stream().map(BDD::getNumberOfNodesAfterReduction).reduce(0.0, Double::sum)
                    / bddList.stream().map(BDD::getNumberOfNodes).reduce(0.0, Double::sum))
                    * 100))
                    + " %");
            System.out.println("Percentage of correct: " + compareStringArrays(bddOutputs, evaluatedOutputs) + "%");
        }

        return new String[]{
                String.valueOf((double) timeOnCreating / 1000000.0),
                String.valueOf((double) timeOnUsing / 1000000.0),
                String.valueOf((double) timeOnEvaluating / 1000000.0),
                String.valueOf(compareStringArrays(bddOutputs, evaluatedOutputs)),
                String.valueOf(bddList.stream().map(BDD::getNumberOfNodes).reduce(0.0, Double::sum)),
                String.valueOf(bddList.stream().map(BDD::getNumberOfNodesAfterReduction).reduce(0.0, Double::sum)),
                String.valueOf(
                        100 - (
                                (bddList.stream().map(BDD::getNumberOfNodesAfterReduction).reduce(0.0, Double::sum)
                                        / bddList.stream().map(BDD::getNumberOfNodes).reduce(0.0, Double::sum))
                                        * 100)
                )
        };
    }


    private static void runLogger() throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\_\\main\\classes\\DSA\\assignment\\binaryDecisionDiagram\\src\\main\\resources\\output_with_best_order.csv");
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        int[] params_size = new int[]{100};
        int[] variables_size = IntStream.range(3, 20).toArray();

        List<String[]> results = new ArrayList<>();

        csvWriter.writeNext(new String[]{"size", "variables", "timeOnCreating", "timeOnUsing", "timeOnEvaluating", "reducingRate"});

        for (int s : params_size) {
            System.out.println("~ Size:" + s);
            for (int value : variables_size) {
                System.out.println("~ Variable:" + value);
                String[] _results = runAutomatic(s, value, true, true);
                results.add(new String[]{
                        String.valueOf(s),
                        String.valueOf(value),
                        _results[0],
                        _results[1],
                        _results[2],
                        _results[6],
//                        _results[4],
//                        _results[5],
                });
            }
        }

        results.forEach(csvWriter::writeNext);
        csvWriter.close();
        fileWriter.close();
    }

    private static double compareStringArrays(List<String[]> list1, List<String[]> list2) {
        int count = 0;

        for (int i = 0; i < list1.size(); i++) {
            if (Arrays.equals(list1.get(i), list2.get(i)))
                count++;
        }

        return Math.ceil((double) count / list1.size()) * 100;
    }
}
