/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 * 
 * @author Hemanth
 *
 */
public class ScheduleTemplateQuery extends BaseQuery {

    private String name;
    private String modeType;
    private String dayOfWeek;
    private String dayOfMonth;
    private String cron;
    private String timerValueHH;
    private String timerValueMM;
    private String month;
    

    public String getModeType() {
        return modeType;
    }


    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

/*
	public String getTimeType() {
		return timeType;
	}


	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
*/

	public String getDayOfWeek() {
		return dayOfWeek;
	}


	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}


	public String getDayOfMonth() {
		return dayOfMonth;
	}


	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}


	public String getCron() {
		return cron;
	}


	public void setCron(String cron) {
		this.cron = cron;
	}
	
	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}
	
	public String getTimerValueHH() {
		return timerValueHH;
	}


	public void setTimerValueHH(String timerValueHH) {
		this.timerValueHH = timerValueHH;
	}
	
	public String getTimerValueMM() {
		return timerValueMM;
	}


	public void setTimerValueMM(String timerValueMM) {
		this.timerValueMM = timerValueMM;
	}


}
