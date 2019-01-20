package com.company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static Map<Character, Boolean> variables;
    private static final String FAILURE = "Undefined var(s) made issue in process";
    private static final String TRUE = "1";
    private static final String FALSE = "0";
    private static String processedBlocksString = "";

    public static void main(String[] args) {

        // Defining file path
        String fileName = "input.txt";
        Path path = Paths.get(fileName);

        // List<String> of file content
        List<String> allLines;

        try {

            byte[] bytes = Files.readAllBytes(path);
            allLines = Files.readAllLines(path, StandardCharsets.UTF_8);

            variables = new HashMap<>();

            for (String line: allLines) {

                if (line.contains("=")) { // if line is defining a variable

                    String[] tempArray = line.split("=");

                    boolean variableValue = tempArray[1].contains("1");

                    // saving all vars with key-value structure
                    variables.put(tempArray[0].charAt(0), variableValue);

                } else {  // if line is requesting output

                    processBlocks(line);

                    System.out.println();
                    System.out.println("Processed Blocks: " + processedBlocksString);

                    System.out.println("Processed Bits: " + processBits(processedBlocksString));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Divides line to calculable blocks (Using recursive algorithm)
     * */
    private static String processBlocks(String line) {

        int BlockEndIndex = 0;

        if (line.length() <= 7) { // if whole line is a single block

            String calculatedBlock = BlockCalculator(line);

            if (calculatedBlock.equals(FAILURE)) return FAILURE;
            else return processedBlocksString += calculatedBlock;
        }
        if (line.charAt(4) == ')') { // block has 5 index (0 - 4)
            BlockEndIndex = 4;
        }
        if (line.charAt(5) == ')') { // block has 6 index (0 - 5)
            BlockEndIndex = 5;
        }
        if (line.charAt(6) == ')') { // block has 7 index (0 - 6)
            BlockEndIndex = 6;
        }

        // calculating first block
        String calculatedBlock = BlockCalculator(line.substring(0, BlockEndIndex + 1));

        // handling result of processing first block
        if (calculatedBlock.equals(FAILURE)) return FAILURE;
        else processedBlocksString += calculatedBlock + line.charAt(BlockEndIndex + 1);

        // skips operator between blocks and process remaining blocks
        return processBlocks(line.substring(BlockEndIndex + 2));
    }

    /**
    * Calculates each single logical block
    * */
    private static String BlockCalculator(String block) {

        Boolean x;
        Boolean y;
        char operator;

        System.out.println("Block :" + block);

        // The whole if-else statement purpose is to set value of boolean vars to true|false|null

        // if we had Not for first var
        if (block.charAt(1) == '!') {
            x = (variables.containsKey(block.charAt(2)))? !variables.get(block.charAt(2)): null;
            operator = block.charAt(3);

            // if we had Not for second var
            if (block.charAt(4) == '!') {
                y = (variables.containsKey(block.charAt(5)))? !variables.get(block.charAt(5)): null;

            } else {
                y = variables.get(block.charAt(4));
            }

        } else {
            x = variables.get(block.charAt(1));
            operator = block.charAt(2);

            // if we had Not for second var
            if (block.charAt(3) == '!') {
                y = (variables.containsKey(block.charAt(4)))? !variables.get(block.charAt(4)): null;

            } else {
                y = variables.get(block.charAt(3));
            }
        }

        String result = logicComputer(x, y, operator);
        System.out.println(result);

        return result;
    }

    /**
     * Calculates several bits (Using recursive algorithm)
     * */
    private static String processBits(String line) {

        boolean x = line.charAt(0) == '1';
        boolean y = line.charAt(2) == '1';
        char operator = line.charAt(1);

//        System.out.println("x: " + x + ", y: " + y + ", operator: " + operator);

        // result: calculated first 2 bits with operator between
        // adding result to line and removing first 3 indexes
        line = logicComputer(x, y, operator) + line.substring(3);

        // if line had only 1 index we have achieved final result :)
        if (line.length() == 1) return line;

        return processBits(line);
    }

    /**
     * Can only process two bits with a operator
     * */
    private static String logicComputer(Boolean x, Boolean y, char operator) {

        String res = "";

//        System.out.println("x: " + x + ", y: " + y + ", operator: " + operator);

        switch (operator) {

            case '&':

                if (x == null || y == null) res = FAILURE;
                else if (x && y) res = TRUE;
                else res = FALSE;

                break;

            case '|':

                if (x == null) {

                    if (y == null) res = FAILURE;
                    else if (y) res = TRUE;
                    else res = FALSE;

                } else if (y == null) {

                    if (x)  res = TRUE;
                    else res = FALSE;

                } else { // if both of characters had value
                    if (x || y) res = TRUE;
                    else res = FALSE;
                }

                break;

            case '@':

                if (x == null || y == null) res = FAILURE;
                else if (( x || y ) && !( x && y )) res = TRUE;
                else res = FALSE;

                break;
        }
        return res;
    }

}
