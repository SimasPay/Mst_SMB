// Copyrighted MFINO 2009
package com.mfino.tools.intlstringcollector;

import com.mfino.tools.intlstringcollector.enums.FileExtensionEnum;
import com.mfino.tools.intlstringcollector.exception.FileDataOutputWriterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the starting point of execution for the I18NTool to kick off.
 * This class collects all the command line arguments that are provided by
 * the user on the console and then pass the same arguments on to I18NTool
 * processor.
 *
 * @author Venkata Krishna Teja D
 */
public class IntlStringCollectorToolMain {

    private String sourceDirectory;
    private String selectedLanguage;
    private String outputFilePath;

    /**
     * Creates an instance of <code>IntlStringCollectorToolMain</code>.
     */
    public IntlStringCollectorToolMain() {
    }

    private String getSourceDir() {
        return sourceDirectory;
    }

    private String getOutputFilePath() {
        return outputFilePath;
    }

    private FileExtensionEnum getFileExtensionEnum() {
        if (null == selectedLanguage || 0 == selectedLanguage.trim().length()) {
            return null;
        }

        if ("JAVA-JSP".equalsIgnoreCase(selectedLanguage.trim())) {
            return FileExtensionEnum.JAVA_JSP_FILE_EXTENSION_ENUM;
        } else if("JS".equalsIgnoreCase(selectedLanguage.trim())) {
            return FileExtensionEnum.JAVA_SCRIPT_FILE_EXTENSION_ENUM;
        } else {
            return null;
        }
    }

    private void getUserInputForSourceDirectory(BufferedReader reader) {
        int failureCount = 0;
        while (true) {
            if (failureCount > 3) {
                // We have given the user 3 fair chances to enter the
                // desired details. Now it's out turn to shutdown the instance.
                System.exit(0);
            }

            System.out.println("Enter the Source Directory Path: ");
            String sourceDirectoryGot = null;

            try {
                sourceDirectoryGot = reader.readLine();
            } catch (IOException e) {
                Logger.getLogger(IntlStringCollectorToolMain.class.getName()).
                        log(Level.SEVERE, null, e);
            }

            if (null == sourceDirectoryGot ||
                    0 == sourceDirectoryGot.trim().length()) {
                // Here prompt the user for the source
                // directory as they have missed it.
                System.out.println("You have not provided the " +
                        "correct input.");
                failureCount++;
                continue;
            }

            // Now evaluate if the path is valid or not.
            File testFile = new File(sourceDirectoryGot);
            if (false == testFile.isDirectory()) {
                // This is not a directory.
                // Ask the user to enter them again.
                System.out.println("You have not entered a " +
                        "valid source directory.");
                failureCount++;
                continue;
            }

            // If we reach here then we have the correct source directory.
            // set the variable
            sourceDirectory = sourceDirectoryGot;
            break;
        }
    }

    private void getUserInputForLanguage(BufferedReader reader) {
        int failureCount = 0;

        while (true) {
            if (failureCount > 3) {
                System.exit(1);
            }
            System.out.println("Enter Java-JSP or JS: ");
            String selectedLanguageGot = null;
            try {
                selectedLanguageGot = reader.readLine();
            } catch (IOException e) {
                Logger.getLogger(IntlStringCollectorToolMain.class.getName()).
                        log(Level.SEVERE, null, e);
            }

            if (null == selectedLanguageGot ||
                    0 == selectedLanguageGot.trim().length()) {
                // Here prompt the user for the source
                // directory as they have missed it.
                System.out.println("You have not provided the " +
                        "correct input.");
                failureCount++;
                continue;
            }

            // If the entered string is not java or js then prompt.
            if (false == "JAVA-JSP".equalsIgnoreCase(selectedLanguageGot.trim()) &&
                    false == "JS".equalsIgnoreCase(selectedLanguageGot.trim())) {
                   
                  // If we reach here then the user dint enter either Java or JS
                // the argument.
                System.out.println("You have not provided the " +
                        "correct input.");
                failureCount++;
                continue;
            }

            // If we reach here then we have the selected language as either
            // Java or JS.
            selectedLanguage = selectedLanguageGot;
            break;
        }
    }

    private void getUserInputForOutputFilePath(BufferedReader reader) {
        int failureCount = 0;

        while (true) {
            if (failureCount > 3) {
                System.exit(2);
            }
            System.out.println("Enter the Output File Directory: ");
            String outputFilePathGot = null;

            try {
                outputFilePathGot = reader.readLine();
            } catch (IOException e) {
                Logger.getLogger(IntlStringCollectorToolMain.class.getName()).
                        log(Level.SEVERE, null, e);
            }

            if (null == outputFilePathGot ||
                    0 == outputFilePathGot.trim().length()) {
                // Here prompt the user for the source
                // directory as they have missed it.
                System.out.println("You have not provided the " +
                        "correct input.");
                failureCount++;
                continue;
            }

            // Now evaluate if the path is valid or not.
            File testFile = new File(outputFilePathGot);
            if (false == testFile.isDirectory()) {
                // This is not a directory.
                // Ask the user to enter them again.
                System.out.println("You have not entered a " +
                        "valid source directory.");
                failureCount++;
                continue;
            }

            outputFilePath = outputFilePathGot;
            break;
        }
    }

    private void getUserInput() {
        // Here we need to get the user input from the console.
        // Create the BufferedReader.
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(System.in));
        getUserInputForSourceDirectory(bufferedReader);
        getUserInputForLanguage(bufferedReader);
        getUserInputForOutputFilePath(bufferedReader);
    }

    /*
     * The starting point of execution for this application.
     */
    public static void main(String[] args) {
        IntlStringCollectorToolMain main = new IntlStringCollectorToolMain();
        main.getUserInput();

        FileExtensionEnum enumSet = main.getFileExtensionEnum();
        if(null == enumSet) {
            System.out.println("The Language that is passed as argument " +
                    "is not java-jsp or js");
            System.exit(2);
        }
        
        // Now we need to get the instantiate the parser.
        IntlStringCollector parser = new IntlStringCollector();
        parser.setFileExtension(main.getFileExtensionEnum());
        parser.setSourceDirectory(main.getSourceDir());
        Hashtable<String, String> dataMap = parser.parseAndReturnData();

        main.generateOutput(dataMap);
    }

    private void generateOutput(Hashtable<String, String> dataMap) {
        FileDataOutputWriter outputWriter = null;
        if (getFileExtensionEnum().equals(
                FileExtensionEnum.JAVA_JSP_FILE_EXTENSION_ENUM)) {
            outputWriter = new JavaFileDataOutputWriter();
        } else if (getFileExtensionEnum().equals(
                FileExtensionEnum.JAVA_SCRIPT_FILE_EXTENSION_ENUM)) {
            outputWriter = new JavaScriptFileDataOutputWriter();
        } else {
            // TODO ::  Handle this situation.
            // This should never happen as we have already restricted
            // the user and application to select some type or the other.
              Logger.getLogger(IntlStringCollectorToolMain.class.getName()).
                        log(Level.SEVERE, null, "Unable to find the Output writer.");
        }


        outputWriter.setDataToWrite(dataMap);
        outputWriter.setOutputFilePath(getOutputFilePath());
        try {
            outputWriter.writeDataToFile();
        } catch (FileDataOutputWriterException fdowe) {
            // TODO :: Log the message.
             Logger.getLogger(IntlStringCollectorToolMain.class.getName()).
                        log(Level.SEVERE, null, fdowe);
          System.err.println("Unable to generate the output file.");
          System.err.println("The reason is: ");
          System.err.println(fdowe.getMessage());
          System.exit(3);
        }
    }
}
