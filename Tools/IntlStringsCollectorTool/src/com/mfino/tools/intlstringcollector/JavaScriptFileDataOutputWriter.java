package com.mfino.tools.intlstringcollector;

import com.mfino.tools.intlstringcollector.exception.FileDataOutputWriterException;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * This class <code>JavaScriptFileDataOutputWriter</code> is responsible to
 * generate the msg.en.js file with the data that needs to be internationalized.
 *
 * @author Venkata Krishna Teja D
 */
public class JavaScriptFileDataOutputWriter extends FileDataOutputWriter {

    private static final String START_STRING = "Ext.ns(\"mFino.msg\");" +
            NEWLINE + "mFino.msg = {" + NEWLINE;
    private static final String END_STRING = "};";

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

        int elementsInDataStore = dataStore.size();
        StringBuffer dataBuffer = new StringBuffer();

        dataBuffer.append(START_STRING);
        int counter = 1;
        while (iterator.hasNext()) {
            String convertionData = iterator.next();
            String fineNameLineNumberDetails = dataStore.get(convertionData);

            // Here the key is the data string and the value is the
            // filename line nunber.
            dataBuffer.append(fineNameLineNumberDetails + NEWLINE);
            dataBuffer.append("\"" + convertionData + "\" : \"" + convertionData +
                    "\"");
            if(counter != elementsInDataStore) {
                dataBuffer.append(",");
            }
            dataBuffer.append(NEWLINE);
            counter++;
        }

        dataBuffer.append(END_STRING);
        return dataBuffer.toString();
    }

    @Override
    public String getFileName() {
        return "msg.English.js";
    }
}
