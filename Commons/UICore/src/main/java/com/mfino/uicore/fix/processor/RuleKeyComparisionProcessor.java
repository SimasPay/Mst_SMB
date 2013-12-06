package com.mfino.uicore.fix.processor;

import com.mfino.fix.processor.IFixProcessor;

/**
 * @author Srikanth
 * 
 * This processor takes Rulekey ID as input and loads the corresponding TxnRuleKeyComparision value(its like a csv
 *  eg., Equal,Less than,Greater than) and splits the csv value and send each resultant value as CMJSRuleKeyComparision entry
 *  to show them as combo values in Additional info grid(UI).
 *  
 *  This is not being used as of now
 *
 */
public interface RuleKeyComparisionProcessor extends IFixProcessor {	


}
