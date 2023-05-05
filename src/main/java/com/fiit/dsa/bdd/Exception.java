package com.fiit.dsa.bdd;

import java.util.Arrays;
import java.util.Random;

import static com.fiit.dsa.bdd.Hash.hash;

public class Exception {

    public static String handleException(String expression, String upper, boolean flag) {
        String lower = upper.toLowerCase();

        if (expression.equals("1") || expression.equals("0"))
            return expression;

        String[] parts = expression.contains("+")
                ? expression.split("\\+")
                : new String[]{expression};
        String[] hash = new String[parts.length * 2];

        for (String part : parts)
            if ((flag && part.equals(lower)) || (!flag && part.equals(upper)))
                return "1";

        if ((flag && expression.equals(lower)) || (!flag && expression.equals(upper)))
            return "1";

        if ((!flag && expression.equals(lower)) || (flag && expression.equals(upper)))
            return "0";

        int temp = 0, count = 0;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isBlank())
                continue;
            if (parts[i].contains(flag ? upper : lower)) {
                if (parts[i].contains(lower) && parts[i].contains(upper)) {
                    if (++count == parts.length)
                        return "0";
                }
                parts[i] = "";
                temp++;
            } else if (parts[i].contains(flag ? lower : upper)) {
                parts[i] = removeLetter(parts[i], flag ? lower : upper);
                if (parts[i].isBlank())
                    temp++;
            }
        }
        temp = parts.length - temp - 1;

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isBlank())
                continue;

            int pos = hash(parts[i], hash.length);
            while (hash[pos] != null) {
                if (hash[pos].equals(parts[i]))
                    break;

                if (++pos >= hash.length)
                    pos = 0;
            }
            if (hash[pos] != null) {
                parts[i] = "";
                temp--;
            } else
                hash[pos] = parts[i];
        }

        lower = expression;
        StringBuilder expressionBuilder = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank())
                continue;
            else
                expressionBuilder.append(part);

            if (temp-- > 0)
                expressionBuilder.append("+");
        }

        expression = expressionBuilder.toString();
        if (expression.isBlank()) {
            if (lower.contains(upper))
                expression = flag ? "0" : "1";
            else
                expression = flag ? "1" : "0";
        }

        return expression;
    }

    public static boolean validateInput(String input) {
        String[] nums = input.split("");

        for (String num : nums) {
            if (!num.equals("0") && !num.equals("1"))
                return false;
        }
        return true;
    }

    public static String[] generateExpression(int varCount) {
        return generateExpression(varCount, 5);
    }

    public static String[] generateExpression(int varCount, int partsSize) {
        StringBuilder order = new StringBuilder();
        StringBuilder expression = new StringBuilder();

        String[] expressionParts = new String[partsSize];
        Arrays.fill(expressionParts, "");

        for (int j = 65; j < 65 + varCount; j++) {
            for (int k = 0; k < expressionParts.length; k++) {
                boolean isInverted = new Random().nextBoolean();
                boolean isLetter = new Random().nextBoolean();

                if (isLetter)
                    expressionParts[k] += (isInverted ? (char) (j + 32) : (char) j);
            }

            order.append((char) j);
        }

        for (int j = 0; j < partsSize; j++) {
            if (expressionParts[j].isBlank())
                continue;

            expression.append(expressionParts[j]).append(j + 1 < partsSize && j + 1 != partsSize ? "+" : "");
        }

        if (!expression.toString().isBlank() && expression.charAt(expression.length() - 1) == '+')
            expression = new StringBuilder(expression.substring(0, expression.length() - 1));

        return new String[]{order.toString(), expression.toString()};
    }

    public static boolean validateVarTypes(String expression, String varTypes) {

        String[] exp = expression.toUpperCase().split("");

        for (int i = 0; i < varTypes.length(); i++) {
            String letter = Character.toString(varTypes.charAt(i));

            for (int j = 0; j < exp.length; j++) {
                if (exp[j].equals(letter) || exp[j].equals("+"))
                    exp[j] = "0";
            }
        }
        for (String s : exp) {
            if (!s.equals("0"))
                return false;
        }
        return true;
    }

    private static String removeLetter(String exp, String letter) {
        exp = exp.replace(letter, "");

        return exp.contains(letter) ? removeLetter(exp, letter) : exp;
    }
}
