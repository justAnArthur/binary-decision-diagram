package com.fiit.dsa;

import lombok.SneakyThrows;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Manager {

    private static final String basePath = "src/main/resources/";

    private final Scanner scanner;

    public Manager() {
        scanner = new Scanner(System.in);
    }

    private Integer handleNumber(String text) {
        System.out.println(text + " `number` :");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception ex) {
            return handleNumber(text);
        }
    }

    private String handleInput(String text, String[] acceptableStringedOptions) {
        System.out.println(text + " [" + Arrays.stream(acceptableStringedOptions).filter(string -> string.charAt(0) != '_').collect(Collectors.joining(", ")) + "]:");

        String line = scanner.nextLine();

        if (line.isEmpty())
            handleInput(text, acceptableStringedOptions);

        return Arrays.stream(acceptableStringedOptions)
                .filter(string -> string.toLowerCase().contains(line.toLowerCase()))
                .findFirst()
                .orElseGet(() -> handleInput(text, acceptableStringedOptions));
    }

    public static void main(String[] args) throws RunnerException {
        Manager manager = new Manager();

        final String methodOfTesting = manager.handleInput("Method of testing:", new String[]{"automatic", "_logger"});

        switch (methodOfTesting) {
            case "automatic":
                runAutomatic();
                break;
            case "manual":
                break;

            case "logger":
                break;
        }
    }

    @SneakyThrows
    private static void runAutomatic() {
        Manager manager = new Manager();

        Options options = new OptionsBuilder()
                .include(Test.class.getSimpleName())
                .timeUnit(TimeUnit.MILLISECONDS)
                .mode(Mode.SingleShotTime)
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(1)
                .result(basePath + "create.csv")
                .resultFormat(ResultFormatType.CSV)
//                .param("size", String.valueOf(manager.handleNumber("Size of expressions")))
//                .param("varCount", String.valueOf(manager.handleNumber("Var count of expression")))
                .param("size", "100", "500", "1000")
                .param("varCount", "3", "5")
                .build();

        new Runner(options).run();
    }
}
