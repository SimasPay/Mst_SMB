// Copyrighted MFINO 2009.
package com.mfino.tools.intlstringcollector;

import com.mfino.tools.intlstringcollector.exception.FileDataOutputWriterException;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * This class <code>JavaFileDataOutputWriter</code> is responsible to generate the
 * msg.en.po file with the data that needs to be internationalized.
 * 
 * @author Venkata Krishna Teja D
 */
public class JavaFileDataOutputWriter extends FileDataOutputWriter {

    private static final String MSG_ID = "msgid ";
    private static final String MSG_STR = "msgstr ";

    @Override
    public String getDataToWrite() throws FileDataOutputWriterException {
        // We have the hashtable which has the KEY AND VALUE as follows:
        // KEY - THE FILE NAME AND THE LINE NUMBER
        // VALUE - THE VALUE THAT IS FOUND.

        Hashtable<String, String> dataStore = getDataStore();
        if(null == getDataStore()) {
            throw new FileDataOutputWriterException("No data to write to file");
        }
        Iterator<String> iterator = dataStore.keySet().iterator();

        StringBuffer dataBuffer = new StringBuffer();

        while (iterator.hasNext()) {
            String theMsgId = iterator.next();
            String fileNameLineNumberField = dataStore.get(theMsgId);

            // now add the stuff to the string buffer.
            dataBuffer.append(fileNameLineNumberField + NEWLINE);
            dataBuffer.append(MSG_ID + "\"" +  theMsgId + "\"" + NEWLINE);
            dataBuffer.append(MSG_STR + "\"" + theMsgId + "\"" + NEWLINE);

        }


        return dataBuffer.toString();
    }

    @Override
    public String getFileName() {
        return "msg.English.po";
    }
}
