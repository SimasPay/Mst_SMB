package com.mfino.uicore.util;

import java.io.Serializable;
import java.text.ParseException;

import org.quartz.CronExpression;

import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Hemanth
 *
 */

public class CronExpressionCreator implements Serializable {

    private static final long serialVersionUID = -1676663054009319677L;
    //static CronExpressionCreator pCron = new CronExpressionCreator();
    boolean recurring;
    boolean SUN;
    boolean MON;
    boolean TUE;
    boolean WED;
    boolean THU;
    boolean FRI;
    boolean SAT;
    public String mode;
    public String minutes;
    public String hours;
    public String dayOfMonth;
    public String month;
    public String dayOfWeek;
   

    public String getCronExpression() {
    	String cronFinalExpression=null;
    	String strMinutes= this.getMinutes();
    	String strHours= this.getHours();
    	String strDayOfMonth= this.getDayOfMonth();
    	String strMonth= this.getMonth();
    	String strDayOfWeek= this.getDayOfWeek();
    	if(isRecurring())
    	{
    		if(this.getMode().equals(CmFinoFIX.ModeType_Minutes.toString()))
    		{
    			if(strMinutes!=null)
    			{
    			strMinutes="0"+"/"+strMinutes;
    			}else{
    				throw new IllegalArgumentException();
    			}
    			strHours="*";
    			strDayOfMonth="*";
    			strMonth="*";
    			strDayOfWeek="?";
    			
    		}
    		else if(this.getMode().equals(CmFinoFIX.ModeType_Hourly.toString()))
    		{
    			
    			strMinutes="0";
    			if(strHours!=null)
    			{
    			strHours="0"+"/"+strHours;
    			}else{
    				throw new IllegalArgumentException();
    			}
    			strDayOfMonth="*";
    			strMonth="*";
    			strDayOfWeek="?";
    		}
    		else if(this.getMode().equals(CmFinoFIX.ModeType_Daily.toString()))
    		{
    			if(strDayOfMonth!=null)
    			{
    			strDayOfMonth="1"+"/"+strDayOfMonth;
    			}else{
    				throw new IllegalArgumentException();
    			}
    			strMonth="*";
    			strDayOfWeek="?";
    		}
    	}
    	else if(!isRecurring()){
    		if(this.getMode().equals(CmFinoFIX.ModeType_Weekly.toString()))
    		{
    			strDayOfMonth="?";

                String daysString = "*";
                StringBuilder sb = new StringBuilder(800);
                boolean moreConditions = false;
                
                if (isSUN()) {
                    sb.append("SUN");
                    moreConditions = true;
                }

                if (isMON()) {
                    if (moreConditions) {
                        sb.append(",");
                    }
                    sb.append("MON");
                    moreConditions = true;
                }

                if (isTUE()) {
                    if (moreConditions) {
                        sb.append(",");
                    }

                    sb.append("TUE");
                    moreConditions = true;
                }

                if (isWED()) {
                    if (moreConditions) {
                        sb.append(",");
                    }

                    sb.append("WED");
                    moreConditions = true;
                }

                if (isTHU()) {
                    if (moreConditions) {
                        sb.append(",");
                    }
                    sb.append("THU");
                    moreConditions = true;
                }

                if (isFRI()) {
                    if (moreConditions) {
                        sb.append(",");
                    }
                    sb.append("FRI");
                    moreConditions = true;
                }

                if (isSAT()) {
                    if (moreConditions) {
                        sb.append(",");
                    }
                    sb.append("SAT");
                    moreConditions = true;
                }

                daysString = sb.toString();
                if(minutes.equals("null") || hours.equals("null") || daysString.equals("null"))
                {
                	throw new IllegalArgumentException();
                }
                else{
                cronFinalExpression = "0 " + minutes + " " + hours + " ? * " + daysString+" *";
                if(CronExpression.isValidExpression(cronFinalExpression)){
                	 return cronFinalExpression;
                }
                else{
                	throw new IllegalArgumentException();
                }
                }
    		}
    		else if(this.getMode().equals(CmFinoFIX.ModeType_Monthly.toString()))
    		{
    			strDayOfWeek="?";
    			strMonth="1"+"/"+strMonth;
    		}
    		else if(this.getMode().equals(CmFinoFIX.ModeType_Hourly.toString()))
    		{
    			strDayOfMonth="*";
    			strMonth="*";
    			strDayOfWeek="?";
    		}
    	}
    	if(strMinutes==null || strHours==null || strDayOfMonth==null || strMonth==null || strDayOfWeek==null)
    	{
    		throw new IllegalArgumentException();
    	}
    	else{
    	cronFinalExpression="0 "+strMinutes+" "+strHours+" "+strDayOfMonth+" "+strMonth+" "+strDayOfWeek+" "+"*";
    	if(CronExpression.isValidExpression(cronFinalExpression)){
       	 return cronFinalExpression;
    	}
    	else{
       	throw new IllegalArgumentException();
       }
    	}
    }

   

    public void setMinutes(String min)
    {
    	int minutes;
    	if(!(("").equals(min)) && min!=null){
    		minutes=Integer.valueOf(min);;
    		if(minutes>=0 && minutes<60)
    		{
    			min = String.valueOf(minutes);
    			this.minutes=min;
    		}else
        	{
        		throw new IllegalArgumentException();
        	}
    	}else if(("").equals(min) || min==null)
    	{
    		min="0";
    		this.minutes=min;
    	}
    	
    }
    public String getMinutes(){
    	return minutes;
    }
    public void setHours(String hrs)
    {
    	int hours;
    	if(!(("").equals(hrs)) && hrs!=null){
    		hours= Integer.valueOf(hrs);
    			if(hours>=0 && hours<24)
    			{
    			hrs=String.valueOf(hours);
    			this.hours=hrs;
    			}
    	    	else{
    	    		throw new IllegalArgumentException();
    	    	}
    	}else if(("").equals(hrs) || hrs==null){
    		hrs="*";
    		this.hours=hrs;
    	}
    	
    	
    }
    public String getHours(){
    	return hours;
    }
    public void setDayOfMonth(String dayOfMonth)
    {
    	int dom;
    	if(!(("").equals(dayOfMonth)) && dayOfMonth!=null){
    		dom=Integer.valueOf(dayOfMonth);
    		if(dom>=1 && dom<=31)
        	{
        		dayOfMonth=String.valueOf(dom);
        		this.dayOfMonth=dayOfMonth;
        	}else{
        		throw new IllegalArgumentException();
        	}
    	}
    	else if(("").equals(dayOfMonth) || dayOfMonth==null)
    	{
    		dayOfMonth="*";
    		this.dayOfMonth=dayOfMonth;
    	}
    	
    }
    public String getDayOfMonth(){
    	return dayOfMonth;
    }
    public void setMonth(String month)
    {
    	int mnth;
    	if(!(("").equals(month)) && month!=null)
    	{
    		mnth=Integer.valueOf(month);
    		if(mnth>=1 && mnth<=12)
        	{
        		month=String.valueOf(mnth);
        		this.month=month;
        	}else{
        		throw new IllegalArgumentException();
        	}
    	}
    	else if(("").equals(month) || month==null)
    	{
    		month="*";
    		this.month=month;
    	}
    	
    }
    public String getMonth(){
    	return month;
    }
    public void setDayOfWeek(String dayOfWeek)
    {
    	int dow;
    	if(!(("").equals(dayOfWeek)) && dayOfWeek!=null)
    	{
    	this.dayOfWeek=dayOfWeek;
    	}
    	
    	else if(("").equals(dayOfWeek) || dayOfWeek==null)
    	{
    		dayOfWeek="?";
    		this.dayOfWeek=dayOfWeek;
    	}
    	
    }
    public String getDayOfWeek()
    {
    	return dayOfWeek;
    }
  
    
    
    public boolean isRecurring() {
        return recurring;
    }
    
    public void setRecurring(boolean recurring) {
    	this.recurring = recurring;
    	if(recurring==true && (CmFinoFIX.ModeType_Weekly.equals(this.getMode())) || (CmFinoFIX.ModeType_Monthly.equals(this.getMode())))
    	{
    		throw new IllegalArgumentException();
    	}
    }
    
    public String getMode(){
    	return mode;

    }
    public void setMode(String mode){
    	this.mode=mode;
    }
    
    

    public boolean isSUN() {
        return SUN;
    }

    public void setSUN(boolean sUN) {
        SUN = sUN;
    }

    public boolean isMON() {
        return MON;
    }

    /**
     * @param mON
     *            the mON to set
     */
    public void setMON(boolean mON) {
        MON = mON;
    }

    public boolean isTUE() {
        return TUE;
    }

    public void setTUE(boolean tUE) {
        TUE = tUE;
    }

    public boolean isWED() {
        return WED;
    }

    public void setWED(boolean wED) {
        WED = wED;
    }

    public boolean isTHU() {
        return THU;
    }

    public void setTHU(boolean tHU) {
        THU = tHU;
    }

    public boolean isFRI() {
        return FRI;
    }

    public void setFRI(boolean fRI) {
        FRI = fRI;
    }

    public boolean isSAT() {
        return SAT;
    }

    public void setSAT(boolean sAT) {
        SAT = sAT;
    }

    public int hashCode() {
        return this.getCronExpression().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof CronExpressionCreator) {
            if (((CronExpressionCreator) obj).getCronExpression()
                    .equalsIgnoreCase(this.getCronExpression())) {
                return true;
            }
        } else {
            return false;
        }
        return false;

    }

}
