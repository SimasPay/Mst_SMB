package corp.dimo.simulate.service;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import corp.dimo.common.models.Logs;

@ManagedBean(name="logsService")
public class LogsService {
	
	private static List<Logs> logs = new ArrayList<Logs>();
	
	public void saveLogs(Logs logsIn){
		logs.add(logsIn);
	}
	
	public List<Logs> retriveLogs(){
		return logs;
	}

}
