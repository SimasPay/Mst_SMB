package com.mfino.hsm.thales7.util;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.util.Log;
import org.jpos.util.LogEvent;
import org.jpos.util.LogListener;
import java.util.Hashtable;

/**
 * Level based filter for logging thales messages
 * This can be enhanced to support complex flitering
 * change logEvent method to filter based on the content
 * @author POCHADRI
 *
 */
public class ThalesLogListener implements LogListener,Configurable
{

    private static Hashtable<String, Integer> levels;

    static{
            levels = new Hashtable<String, Integer>(6);
            levels.put(Log.TRACE, 1);
            levels.put(Log.DEBUG, 2);
            levels.put(Log.INFO, 3);
            levels.put(Log.WARN, 4);
            levels.put(Log.ERROR, 5);
            levels.put(Log.FATAL, 6);
    }

    private String level = Log.INFO;

    public ThalesLogListener() {
        super();
    }

    public void setConfiguration (Configuration cfg)
        throws ConfigurationException
    {
        try {
            String log_level = cfg.get("level");
            if ( (log_level != null) && (!log_level.trim().equals("")) )
            {
                if (levels.containsKey(log_level))
                    level = log_level;
            }
        } catch (Exception e) {
            throw new ConfigurationException (e);
        }
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean permitLogging(String tagLevel)
    {
        Integer I = (Integer)levels.get(tagLevel);

        if (I == null)
            I = (Integer)levels.get(Log.INFO);

        Integer J = (Integer)levels.get(level);

        return (I >= J);
    }

    public synchronized LogEvent log(LogEvent ev) 
    {
        if (permitLogging(ev.getTag()))
        {
           return ev;
        }    	
        return null;
    }
}