/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.jsfileconcatenator;

import com.mfino.jsfileconcatenator.constants.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author sandeepjs
 */
public class JSFileConcatenator {

    private String jspfFilePath;
    private String jsDirParent;
    private String outputFile;

    public JSFileConcatenator(String jspfFilePath, String jsDirParent, String outputFile) {

        this.jsDirParent = jsDirParent;
        this.jspfFilePath = jspfFilePath;
        this.outputFile = outputFile;

    }

    public void concatenateFiles() throws Exception {
        ArrayList<File> jsFileList = readJspf(jspfFilePath);
        processFilesList(jsFileList, outputFile);
    }

    private String processLine(String line) throws Exception {
        String fileName = null;

        if (line.startsWith(Constants.SCRIPT_ELEMENT_START)) {
            int indexStart = line.indexOf(Constants.SRCCURL_VALUE) + Constants.SRCCURL_VALUE.length();
            int endIndex = line.indexOf(Constants.JSFILE_SUFFIX) + Constants.JSFILE_SUFFIX.length() - 1;
            fileName = line.substring(indexStart, endIndex);
            System.out.println(fileName);

        }
        return fileName;
    }

    private ArrayList<File> readJspf(String jspfFilePath) throws Exception {

        ArrayList<File> retList = new ArrayList<File>();

        File jspfFile = new File(jspfFilePath);
        FileReader fileReader = new FileReader(jspfFile);
        BufferedReader br = new BufferedReader(fileReader);
        String tmp;
        tmp = br.readLine();
        while (tmp != null) {

            String retValue = processLine(tmp);
            if (retValue != null) {
                retList.add(new File(jsDirParent + retValue));
            }
            tmp = br.readLine();
        }

        fileReader.close();
        br.close();

        return retList;
    }

    private void processFilesList(ArrayList<File> jsFileList, String outputFile) throws Exception {


        System.out.println("size = " + jsFileList.size());

        File outputFileObj = new File(outputFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileObj));

        Iterator<File> it = jsFileList.iterator();

        while (it.hasNext()) {

            File file = it.next();

            System.out.println(file.getName());

            writer.write(Constants.LINE_END);
            writer.write(Constants.LINE_END);

            writer.write(Constants.COMMENT_START + file.getName());
            writer.write(Constants.COMMENT_END);

            writer.write(Constants.LINE_END);
            writer.write(Constants.LINE_END);



            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String tmp;
            tmp = br.readLine();

            while (tmp != null) {

                writer.write(tmp);
                writer.write(Constants.LINE_END);

                tmp = br.readLine();
            }


            fileReader.close();
            br.close();

        }

        writer.flush();
        writer.close();
    }
}
