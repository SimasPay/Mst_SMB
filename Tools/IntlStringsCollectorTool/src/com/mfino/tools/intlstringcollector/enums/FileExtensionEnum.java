// Copyrighted MFINO 2009.
package com.mfino.tools.intlstringcollector.enums;

/**
 * This class <code>FileExtensionEnum</code> defines the file extension type
 * and return in the form of enum.
 * 
 * @author Venkata Krishna Teja D
 */
public class FileExtensionEnum {

    public static FileExtensionEnum JAVA_JSP_FILE_EXTENSION_ENUM = new FileExtensionEnum(".java", ".jsp");
    public static FileExtensionEnum JAVA_SCRIPT_FILE_EXTENSION_ENUM = new FileExtensionEnum(".js");
   
    private String extensionType;
    private String secondExtensionType;
    private boolean hasSecondExtension;

    private FileExtensionEnum(String extensionType) {
        this.extensionType = extensionType;
    }

    private FileExtensionEnum(String extensionType, String secondExtensionType) {
        this.extensionType = extensionType;
        this.secondExtensionType = secondExtensionType;
        hasSecondExtension = true;
    }

    public boolean hasSecondExtensionType() {
        return hasSecondExtension;
    }

    public String getSecondExtensionType() {
        return secondExtensionType;
    }

    public String getExtensionType() {
        return extensionType;
    }
}
