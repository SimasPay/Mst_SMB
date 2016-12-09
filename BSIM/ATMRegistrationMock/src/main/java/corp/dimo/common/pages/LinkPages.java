package corp.dimo.common.pages;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@ManagedBean
public class LinkPages {
	
	@PostConstruct
    public void init() {
		
	}
	
	public String mBankingRegistration(){
		return "atmSimulatorTransaction_mBankingRegistration";
	}

}
