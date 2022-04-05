package edu.nwpu.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;

public class Testfile {
    public static void main(String[] args) {
        int i = 0;
//        String fileName = "C:\\Users\\晓哥\\IdeaProjects\\busshiftdata\\src\\test\\java\\edu\\nwpu\\plateID.txt";
        String fileName = "src/test/java/edu/nwpu/plateID.txt";
//        String fileName = "plateID.txt";

        File file = new File(fileName);
        LineIterator lineIterator = null;

        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            System.out.println(line);
        }
    }
}
