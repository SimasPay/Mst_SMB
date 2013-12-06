/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.exceptions.LocaleDataLoadException;
import com.mfino.util.ConfigurationUtil;

/**
 * All user facing text should go through this method. This is a GETTEXT-like
 * implimentation of I18N. The differences is that we need to dynamically
 * show the application in different locale.
 *
 * @author xchen
 */
public class MessageText {
	
	private static Logger log = LoggerFactory.getLogger(MessageText.class);

    private static Hashtable<String, Hashtable<String, String>> localeDirectory =
            new Hashtable<String, Hashtable<String, String>>();


    public static void load(String localeToLoad) throws LocaleDataLoadException {
        //This should load a partically locale's PO localFileToRead into memory
        Hashtable<String, String> localeDataContents  = new Hashtable<String, String>();

        // Now read the localFileToRead
        String fileName = "msg." + localeToLoad + ".po";

        File localFileToRead = new File(ConfigurationUtil.getI18NMessagePath(), fileName);
        
        // Creating the input stream to read the locale data file.
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(localFileToRead);
        } catch (FileNotFoundException ex) {
            String errorMsg = "The locale data file " + fileName + " not found.";
            log.error(errorMsg, ex);
            throw new LocaleDataLoadException(errorMsg, ex);
        }
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String msgId = null;
        while(true) {
            String eachLine = null;
            try {
                eachLine = bufferedReader.readLine();
            } catch (IOException ex) {
               // Do nothing just ignore the case and continue reading the rest.
               // TODO :: Log error message
            	log.warn("Error occurred while loading file: ", ex);
               break;
            }

            if(null == eachLine ) {
                break;
            } else if(0 == eachLine.length()) {
                // We got an empty line
                continue;
            }

            // If we reach here then we have the string line.
            // Extract the data and put it into the hashtable appropriately.
            // NOTE :
            // The hastable shd have msgid=msgstr as the record.
            if(eachLine.startsWith("msgid ")) {
                // Here get the msg id and set it to the string.
                msgId = eachLine.substring(eachLine.indexOf(" ") + 1, eachLine.length());
            } else if(eachLine.startsWith("msgstr ")) {
                // here get the msgstr and put both the msgid and msgstr in the hashtable.
                String msgStr = eachLine.substring(eachLine.indexOf(" ") + 1, eachLine.length());
                if(null == msgStr || 0 == msgStr.length()) {
                    msgId = null;
                    continue;
                }
                // If we reach here then we have the msgid and msg id.
                //put it into hashtable.
                localeDataContents.put(msgId, msgStr);
                // now clear msgid
                msgId = null;
            } 
        }
        
        localeDirectory.put(localeToLoad, localeDataContents);
    }

    public static String _(String messageKey) {
        //Looking up the correct text with the user's language setting
        //and then returns the correct string.
        //return _(messageKey, UserService.getUserLanguageCode());
    	return messageKey;
    }

    public static String _(String messageKey, String currentLocale) {
        //first check if the requested locale resource is available or not
        //if it is not available, load the resource localFileToRead into memory (for performance)
        //return the the message from the resource localFileToRead loaded for the locale
        //if no message is found, return the message key itself
        if (localeDirectory.get(currentLocale) == null) {
            try {
                load(currentLocale);
            } catch (LocaleDataLoadException ex) {
            	log.debug("Error loading locale data:", ex);
                localeDirectory.put(currentLocale, new Hashtable<String, String>());
            }
        }

        Hashtable<String, String> messageTable = localeDirectory.get(currentLocale);
        String localeMessage = messageTable.get(messageKey);
        if (localeMessage != null) {
            return localeMessage;
        } else {
            return messageKey;
        }
    }
}
