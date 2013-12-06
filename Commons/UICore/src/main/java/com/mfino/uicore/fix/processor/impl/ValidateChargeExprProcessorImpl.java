package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMJSValidateChargeExpr;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ValidateChargeExprProcessor;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

@Service("ValidateChargeExprProcessorImpl")
public class ValidateChargeExprProcessorImpl extends BaseFixProcessor implements ValidateChargeExprProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		CMJSValidateChargeExpr realMsg = (CMJSValidateChargeExpr) msg;
		String expr = realMsg.getCharge();
		try {
			expr = expr.replaceAll("%", "/100");
			Calculable calc = new ExpressionBuilder(expr).withVariable(
					"amount", 200).build();
			double result = calc.calculate(); // This line is useful to check if calculation throws "Stack Empty Exception"
		} catch (UnknownFunctionException e) { //Error description will be sent in "error message(if any) : exception name" format 
											   //so that in js we can split message on ":" and show only message part before ":"
			realMsg.setErrorDescription(MessageText._(e.getMessage()) + ":");
		} catch (UnparsableExpressionException e) {
			realMsg.setErrorDescription(MessageText._(e.getMessage()) + ":");
		} catch (Exception e) {
			realMsg.setErrorDescription(":" + MessageText._(e.toString()));
		}
		return realMsg;
	}
}
