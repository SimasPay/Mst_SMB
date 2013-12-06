/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Diwakar
 */
public class NotificationMessageParserTest {

    public static void main(String args[]) {
        List<String> notificationText = new ArrayList<String>();
        notificationText.add("Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $((TransactionID))");
        notificationText.add("Sorry, transaction on $(TransactionDateTime)$ failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $((TransactionID))");
        notificationText.add("$(TransactionDateTime) Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(TransactionDateTime)) Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(TransactionDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(Trans[actionD]ateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(Trans(actionD]ateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$([TransactionDateTime])Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(Transacti$onDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(test)$(TransactionDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$()$(TransactionDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(967)$(TransactionDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");
        notificationText.add("$(_96_7_)$(TransactionDateTime)Sorry, transaction on failed. An error occurred while processing your request. Info, call $$(CustomerServiceShortCode). REF: $(TransactionID)");

        Pattern p = Pattern.compile("\\$\\([\\w]+\\)");
        for (String s : notificationText) {
            List<TextPart> textParts = new ArrayList<TextPart>();
            Matcher matcher = p.matcher(s);
            int lastIndex = 0;
            while (matcher.find()) {
                String preMatchedString = s.substring(lastIndex, matcher.start());
                String postMatchedString = s.substring(matcher.start() + 2, matcher.end() - 1);
                if (preMatchedString.length() > 0) {
                    textParts.add(new TextPart(preMatchedString, false));
                }
                if (postMatchedString.length() > 0) {
                    textParts.add(new TextPart(postMatchedString, true));
                }
                lastIndex = matcher.end();
            }
            String finalMatchedString = s.substring(lastIndex);
            if (finalMatchedString.length() > 0) {
                textParts.add(new TextPart(finalMatchedString, false));
            }
            System.out.println("Input String: " + s);
            System.out.println("Output:");
            for (TextPart text : textParts) {
                System.out.println(text.text);
            }
        }
    }
}

class TextPart {
//		int	length	=	0;
//		int	offset	=	0;

    String text;
    boolean isVariable = false;

    /**
     *
     */
    public TextPart(String text, boolean isVariable) {
        this.text = text;
        this.isVariable = isVariable;
    }
}
