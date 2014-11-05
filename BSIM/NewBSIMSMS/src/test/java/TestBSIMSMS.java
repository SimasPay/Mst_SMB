import com.mfino.bsim.sms.impl.BSIMSMSServiceImpl;
import com.mfino.mce.notification.SMSNotificationService;


public class TestBSIMSMS {
	
	public static void main(String[] args){
		BSIMSMSServiceImpl bsimSMSService = new BSIMSMSServiceImpl();
		bsimSMSService.setUrl("http://uat.banksinarmas.com/SmsAlert/app/sendSms/systemId/tokenId/mdn/message");
		bsimSMSService.setSystemId("TEST");
		bsimSMSService.setTokenId("123456");
		bsimSMSService.setMdn("628812345678");
		bsimSMSService.setMessage("test message");
		bsimSMSService.process(null);
	}

}
