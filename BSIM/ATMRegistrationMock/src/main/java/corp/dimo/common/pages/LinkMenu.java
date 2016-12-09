package corp.dimo.common.pages;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@ManagedBean
public class LinkMenu {
	
	@PostConstruct
    public void init() {
		
	}
	
	public String atmSimulator(){
		return "atmSimulatorMenu";
	}
	
	public String common_simulate_mfsServer(){
		return "common_simulate_mfsServer";
	}

}
