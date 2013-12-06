package com.mfino.tools.intlstringcollector;

import com.mfino.tools.intlstringcollector.enums.FileExtensionEnum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class <code>IntlStringCollector</code> is responsible to scan and collect the data
 * that needs to be internationalized from the detected files.
 * 
 * @author Venkata Krishna Teja D
 */
public class IntlStringCollector {

    private static final String NEWLINE =
            System.getProperty("line.separator", "\n");


    private String sourceDirectory;
    private FileExtensionEnum fileExtensionEnum;
    private Hashtable<String, String> msgsMap;

    public IntlStringCollector() {
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void setFileExtension(FileExtensionEnum fileExtensionEnum) {
        this.fileExtensionEnum = fileExtensionEnum;
    }

    private String getSourceDirectory() {
        return sourceDirectory;
    }

    private FileExtensionEnum getFileExtension() {
        return fileExtensionEnum;
    }

    public Hashtable<String, String> parseAndReturnData() {
        // Here we need to loop through and get all the files in the
        // directory and then look in each file and get the patterns out.
        msgsMap = new Hashtable<String, String>();
        parseAllData(getSourceDirectory());
        return msgsMap;
    }

    private void parseAllData(String sourceDirectory) {
       // First get all the files in this dir.
        File dir = new File(sourceDirectory);

        File[] theFilesInThisDir = dir.listFiles();

        if(null == theFilesInThisDir) {
            return;
        }
        
        for (int i = 0; i < theFilesInThisDir.length; i++) {
            File eachFile = theFilesInThisDir[i];
            if (eachFile.isDirectory()) {
                try {
                    parseAllData(eachFile.getCanonicalPath());
                } catch (IOException e) {
                    //  LOg here
                    Logger.getLogger(IntlStringCollector.class.getName()).
                        log(Level.SEVERE, null, e);
                    continue;
                }
            } else {
                if (eachFile.getName().endsWith(getFileExtension().getExtensionType())) {
                    parseDataInFile(eachFile);
                }else if(true == getFileExtension().hasSecondExtensionType()) {
                    if(eachFile.getName().endsWith(getFileExtension().getSecondExtensionType())) {
                        parseDataInFile(eachFile);
                    }
                }
            }
        }
    }

    private void parseDataInFile(File file) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
        } catch (FileNotFoundException e) {
              Logger.getLogger(IntlStringCollector.class.getName()).
                        log(Level.SEVERE, null, e);
            return;
        }
        String fileName = file.getAbsolutePath();

        // TODO :: This code doesn't handle the Multiple lines marker method presence.
        //          This code currently check if the marker method starts and ends in the same line.
        // (TEJA) :: Complete this.
        int lineNumber = 0;
        while (true) {
            String eachLine = null;
            try {
                eachLine = bufferedReader.readLine();
            } catch (IOException e) {
                // Log the error.
                Logger.getLogger(IntlStringCollector.class.getName()).
                        log(Level.SEVERE, null, e);
                break;
            }

            if (null == eachLine) {
                // TODO :: Log the error.
                break;
            }


            // Append the line number here.
            lineNumber++;

            if (0 == eachLine.trim().length()) {
                // In the source file we can expect the new lines which are
                // empty like the one above the if statement.
                // Append the line number.
                continue;
            }

            String fileNameLineNumber = "// " + fileName + " line number - " +
                    lineNumber + " ";
            // now we have the line try to get the pattern matches.
            Pattern regex = Pattern.compile("_\\(.*?\\)");

            Matcher regexMatcher = regex.matcher(eachLine);

            while (regexMatcher.find()) {
                // Now we have multiple matches in this line.
                String match = regexMatcher.group();
                String parsedMatch = extractTheMessageFromMatch(match);
                if(null == parsedMatch) {
                    continue;
                }

                setToMap(parsedMatch, fileNameLineNumber);
            }
        }
    }

    private String extractTheMessageFromMatch(String theMatch) {
        System.out.println("Match to extract <" +theMatch + ">");
        theMatch = theMatch.trim();
        String delimiter = null;
        if(theMatch.contains("\"")) {
            delimiter = ("\"");
        } else {
            delimiter = ("'");
        }

        int indexOfStartQuotation = theMatch.indexOf(delimiter);
        int indexOfEndQuotation = theMatch.lastIndexOf(delimiter);

        if(indexOfStartQuotation <= 0) {
            return null;
        }

        if(indexOfEndQuotation <=1) {
            return null;
        }

        if(indexOfStartQuotation + 1 >= indexOfEndQuotation) {
            return null;
        }

        String exactMatch = theMatch.substring(indexOfStartQuotation + 1,
                indexOfEndQuotation);
        if(null == exactMatch) {
            return null;
        }
        exactMatch = exactMatch.replaceAll("/", "");
        System.out.println("Extracted String <" + exactMatch + ">");
        return exactMatch;
    }

    private void setToMap(String match, String fileNameLineNumber) {
        // here the parsedMatch is the key and the filename line number is the
        // value. This ways we can eliminate the duplicate msgids.
        // if there is already key set in the hastable then
        // updating the value by appending the fileNameLineNumber.
        if(true == msgsMap.containsKey(match)) {
            // Here we append the value with the fileNameLineNumber.
            String exitingValue = msgsMap.get(match);
            msgsMap.put(match, exitingValue +" "+ NEWLINE + fileNameLineNumber);
        } else {
            // Here we need to set the key and value as this is the first entry.
            msgsMap.put(match, fileNameLineNumber);
        }
    }
}
