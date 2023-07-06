package com.translator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void measureMemoryUsage() {
        // Get the runtime instance
        Runtime runtime = Runtime.getRuntime();

        // Print the memory usage statistics
        long totalMemory = runtime.totalMemory()/ 1048576;
        long freeMemory = runtime.freeMemory()/ 1048576;
        long usedMemory = (totalMemory - freeMemory);

        System.out.println("Total Memory: " + totalMemory + " MB");
        System.out.println("Free Memory: " + freeMemory + " MB");
        System.out.println("Used Memory: " + usedMemory + " MB");
}

    public static void append(BufferedWriter writer, boolean flag, String last_char) throws IOException {
        if(flag){
            writer.write(last_char+" ");
            flag = false;
        }
        else {
            writer.write(" ");
        }
    }
    public static void main(String[] args) {

        measureMemoryUsage();

        long startTime = System.currentTimeMillis();
        boolean flag = false;
        ArrayList<String> EnWords = new ArrayList<>();
        HashMap<String, Integer> WordFrequency = new HashMap<>();
        HashMap<String, String> FrenchDict = new HashMap<>();

        String find_word_file = "src/com/translator/find_words.txt";
        String t8_shakeseare_file = "src/com/translator/t8.shakespeare.txt";
        String french_dictionary = "src/com/translator/french_dictionary.csv";

        try{
            File file = new File(find_word_file);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                String word = scanner.nextLine();
                EnWords.add(word);
                WordFrequency.put(word,0);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("An error occurred!");
            e.printStackTrace();
        }

        try {
            File french = new File(french_dictionary);
            Scanner scan = new Scanner(french);
            String splitter = ",";
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String []word = line.split(splitter);
                FrenchDict.put(word[0], word[1]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        try{
            File file = new File(t8_shakeseare_file);
            Scanner scanner = new Scanner(file);

            File translate = new File("src/com/translator/translate.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(translate));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] words=line.split(" ");
                for(String word : words){
                    String last_char="";
                    if(word.length()>2) last_char=word.substring(word.length()-1);
                    if(last_char.equals(".") || last_char.equals(",") || last_char.equals("!") || last_char.equals("?") || last_char.equals(";") || last_char.equals(":")){
                        flag = true;
                        word = word.substring(0,word.length()-1);
                    }

                    String lower = word.toLowerCase();
                    if(EnWords.contains(lower)){
                        int count = WordFrequency.get(word.toLowerCase())+1;
                        WordFrequency.put(word.toLowerCase(), count);
                        String capitalize = (word.substring(0,1).toUpperCase())+(word.substring(1)).toLowerCase();

                        String french_word = FrenchDict.get(lower);
                        String french_word_capitalized =  (french_word.substring(0,1).toUpperCase())+(french_word.substring(1).toLowerCase());
                        if(word.equals(lower)){
                            writer.write(FrenchDict.get(word.toLowerCase()));
                            append(writer, flag, last_char);
                        } else if (word.equals(capitalize)) {
                            writer.write(french_word_capitalized);
                            append(writer, flag, last_char);
                        } else{
                            writer.write(FrenchDict.get(word.toLowerCase()).toUpperCase());
                            append(writer, flag, last_char);
                        }
                    }
                    else {
                        writer.write(word);
                        if(flag){
                            writer.write(last_char+" ");
                            flag = false;
                        }
                        else {
                            writer.write(" ");
                        }
                    }
                }
                writer.newLine();
            }
        }
        catch (FileNotFoundException e){
            System.out.println("An error occurred!");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            File output = new File("src/com/translator/frequency.csv");
            //output.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            File file = new File(french_dictionary);
            Scanner scanner = new Scanner(file);
            writer.write("English Word,French Word,Frequency");
            writer.newLine();
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] words=line.split(",");

                String EnglishWord = words[0];
                String FrenchWord = words[1];

                writer.write(EnglishWord+","+FrenchWord+","+WordFrequency.get(EnglishWord));
                writer.newLine();

            }
        }
        catch (FileNotFoundException e){
            System.out.println("An error occurred!");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        for(String word: EnWords){
//            System.out.println(word+" "+WordFrequency.get(word));
//        }
        System.gc();
        measureMemoryUsage();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        double minutes = (double) elapsedTime / (1000 * 60);
        double seconds = (double) elapsedTime / 1000;
        System.out.println("Program execution time: " + minutes + " minutes and "+seconds);
    }
}