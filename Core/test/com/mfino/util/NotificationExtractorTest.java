package com.mfino.util;

import java.util.List;

import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.util.StringWordDiffUtil.DiffPair;


public class NotificationExtractorTest {
	
	@Test
	public void test1() {
		String constructedText ="1(128) Sorry, transaction on 26/11/10 19:36 failed. M-Commerce service on destination number is not active. Info, call 881. REF:1238844";
		List<DiffPair> vars = NotificationVariableExtractor.getVariableValues(
				constructedText, CmFinoFIX.Language_English);
		for(DiffPair pair : vars)
			System.out.println(pair);
	}
	
	@Test
	public void test2() {
		String constructedText ="0(76) Share load 50000 to 628814325667 on 26/11/10 19:36 successful. Thank you. REF: 124567 ";
		List<DiffPair> vars = NotificationVariableExtractor.getVariableValues(
				constructedText, CmFinoFIX.Language_English);
		for(DiffPair pair : vars)
			System.out.println(pair);
	}

}
