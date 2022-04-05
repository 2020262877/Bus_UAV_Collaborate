package edu.nwpu.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class dataWriteTest {

    /**
     * 转置函数
     *
     * @throws IOException
     */
    @Test
    public void dataManage() throws IOException {
        int[][] matrix = new int[7457][36];
        int[][] reverseMatrix = new int[36][7457];
        File file = new File("D:\\fuckR\\datasets\\duplicateDataset.csv");
        LineIterator lineIterator = null;
        int i = 0;

        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (lineIterator.hasNext()) {
            int j = 0;
            String line = lineIterator.nextLine();
            String[] rowValue = line.split(",");
            for (String value : rowValue)
                matrix[i][j++] = Integer.parseInt(value);
            ++i;
        }
        for (int j = 0; j < matrix.length; j++)
            for (int k = 0; k < matrix[0].length; k++)
                reverseMatrix[k][j] = matrix[j][k];

        File newFile = new File("D:\\fuckR\\datasets\\tumor.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));

        for (int j = 0; j < reverseMatrix.length; j++) {
            String line = "";
            for (int k = 0; k < reverseMatrix[0].length - 1; k++)
                line += "\"" + reverseMatrix[j][k] + "\"" + ",";
            line += "\"" + reverseMatrix[j][reverseMatrix[0].length - 1] + "\"";
            bw.write(line);
            bw.newLine();
        }
        bw.close();
    }
}
