package com.mfino.provision.tools.propertymanager;

import java.io.File;
import java.io.InputStream;

public class PMObject
{
	private boolean Debug = false;
	private boolean useSalt = false;
	private boolean MakeChangesAtProduction = false;
	private boolean allFilePropManualChange = true;
	private boolean deployVerification = false;
	private String LogFileLocation;
	private String ConfigFileLocation;
	private File FileChecklist;
	private Passwords password;
	private InputStream fstreamChecklist;

	public Passwords getPassword()
	{
		return this.password;
	}

	public boolean isDeployVerification()
	{
		return this.deployVerification;
	}

	public void setDeployVerification(boolean deployVerification)
	{
		this.deployVerification = deployVerification;
	}

	public void setPassword(Passwords password)
	{
		this.password = password;
	}

	public boolean isUseSalt()
	{
		return this.useSalt;
	}

	public void setUseSalt(boolean useSalt)
	{
		this.useSalt = useSalt;
	}

	public boolean isDebug()
	{
		return this.Debug;
	}

	public void setDebug(boolean debug)
	{
		this.Debug = debug;
	}

	public InputStream getFstreamChecklist()
	{
		return this.fstreamChecklist;
	}

	public void setFstreamChecklist(InputStream fstreamChecklist)
	{
		this.fstreamChecklist = fstreamChecklist;
	}

	public String getLogFileLocation()
	{
		return this.LogFileLocation;
	}

	public void setLogFileLocation(String logFileLocation)
	{
		this.LogFileLocation = logFileLocation;
	}

	public File getFileChecklist()
	{
		return this.FileChecklist;
	}

	public void setFileChecklist(File fileChecklist)
	{
		this.FileChecklist = fileChecklist;
	}

	public boolean isMakeChangesAtProduction()
	{
		return this.MakeChangesAtProduction;
	}

	public void setMakeChangesAtProduction(boolean makeChangesAtProduction)
	{
		this.MakeChangesAtProduction = makeChangesAtProduction;
	}

	public String getConfigFileLocation()
	{
		return this.ConfigFileLocation;
	}

	public void setConfigFileLocation(String configFileLocation)
	{
		this.ConfigFileLocation = configFileLocation;
	}

	public boolean isAllFilePropManualChange()
	{
		return this.allFilePropManualChange;
	}

	public void setAllFilePropManualChange(boolean allFilePropManualChange)
	{
		this.allFilePropManualChange = allFilePropManualChange;
	}

}
