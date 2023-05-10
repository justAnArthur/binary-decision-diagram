package com.fiit.dsa.bdd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Expression {

    private static final Random random = new Random();


    /**
     * @param input Input to be validated
     * @return True if input is valid, false otherwise
     * {@code @todo} Change on regex
     */
    public static boolean validateInput(String input) {
        String[] nums = input.split("");

        for (String num : nums) {
            if (!num.equals("0") && !num.equals("1"))
                return false;
        }
        return true;
    }

    public static String[] generateExpression(int numberOfVariables) {
        return generateExpression(numberOfVariables, (int) (Math.random() * 5 + 2));
    }

    /**
     * @param numberOfVariables Number of variables
     * @param numberOfParts     Number of parts in expression
     * @return Generated expression
     */
    public static String[] generateExpression(int numberOfVariables, int numberOfParts) {
        int length = random.nextInt(10) + numberOfVariables + numberOfParts;

        List<Character> expressionCharacters = new ArrayList<>();

        for (int i = 0; i < numberOfVariables; i++) {
            expressionCharacters.add((char) (i + (random.nextBoolean() ? 'A' : 'a')));
        }

        for (int i = 0; i < length - numberOfVariables; i++) {
            expressionCharacters.add((char) (random.nextInt(numberOfVariables) + (random.nextBoolean() ? 'A' : 'a')));
        }

        Collections.shuffle(expressionCharacters);

        // Add plus signs randomly between characters
        for (int i = 0; i < numberOfParts - 1; i++) {
            int index = random.nextInt(expressionCharacters.size() - 1) + 1;
            while (expressionCharacters.get(index - 1) == '+' || expressionCharacters.get(index) == '+') {
                index = random.nextInt(expressionCharacters.size() - 1) + 1;
            }
            expressionCharacters.add(index, '+');
        }

        StringBuilder variables = new StringBuilder();
        for (int i = 0; i < numberOfVariables; i++) {
            variables.append((char) (i + 'A'));
        }

        return new String[]{
                variables.toString(),
                expressionCharacters.stream().map(String::valueOf).collect(Collectors.joining(""))
        };
    }

    /**
     * @param expression Expression to be validated
     * @param varTypes   Variable types
     * @return False if expression is valid, true otherwise
     */
    public static boolean validateVarTypes(String expression, String varTypes) {

        // Build a regular expression pattern from the second string
        String escaped = Pattern.quote(varTypes.toUpperCase());

        String pattern = "[" + escaped + "+]*";

        // Check if the expression string matches the pattern
        return !varTypes.matches("[" + escaped + "]+") || !expression.toUpperCase().matches(pattern);
    }

    public static String evaluateExpression(String expression, String order) {

        String[] variables = order.split("");

        for (int i = 0; i < variables.length; i++) {
            expression = expression.replaceAll(Character.toString((char) ('A' + i)), variables[i]);
            expression = expression.replaceAll(Character.toString((char) ('a' + i)), String.valueOf(1 - Integer.parseInt(variables[i])));
        }

        Integer result = null;

        String[] parts = expression.split("\\+");
        for (String part : parts) {
            Integer _result = null;

            String[] _parts = part.split("");
            for (String s : _parts) {
                if (_result != null)
                    _result &= Integer.parseInt(s);
                else
                    _result = Integer.parseInt(s);
            }

            if (result != null)
                result |= _result;
            else
                result = _result;
        }

        return String.valueOf(result);
    }

    public static String[] generateOrderVariants(String variables) {
        List<String> variants = new ArrayList<>();
        generateOrderVariants("", variables, variables.length(), variants, variables.length());
        return variants.toArray(new String[0]);
    }

    public static int generateOrderVariants(String prefix, String chars, int length, List<String> variants, int limit) {
        if (variants.size() == limit) {
            return limit; // stop generating new variants once we reach the limit
        }
        if (prefix.length() == length) {
            variants.add(prefix);
            return variants.size();
        }
        int count = 0;
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (!prefix.contains(String.valueOf(c))) { // check if the character has already been used
                count = generateOrderVariants(prefix + c, chars, length, variants, limit);
                if (count == limit) {
                    break; // stop generating new variants once we reach the limit
                }
            }
        }
        return count;
    }
}
