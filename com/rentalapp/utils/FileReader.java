package com.rentalapp.utils;

import java.io.*;
import java.util.*;

public class FileReader {
    public static List<String[]> readCSV(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                data.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file " + filename + ": " + e.getMessage());
        }
        return data;
}

    public static List<String> readTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading text file " + filename + ": " + e.getMessage());
        }
        return lines;
    }
}