package com.mfino.mce.fix;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.isotofix.BalanceInquiry;


public class FIXAggregationStragety implements AggregationStrategy
{
	Log log = LogFactory.getLog(FIXAggregationStragety.class);
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) 
	{
        if (oldExchange == null) {
            // first time through, the oldExchange is null,
            // so just return newExchange as there is nothing to merge
        	log.info("Got null old exchange in fix aggregator\n");
            return newExchange;
        }
        
        
        return newExchange;
    }

}
