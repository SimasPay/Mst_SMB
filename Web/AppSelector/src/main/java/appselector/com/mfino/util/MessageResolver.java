package appselector.com.mfino.util;
 
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageResolver {
	
	private ResourceBundleMessageSource resourceBundle;

	public MessageResolver(){
	}
	
	public MessageResolver(ResourceBundleMessageSource bundle){
		resourceBundle = bundle;
	}
	
	public ResourceBundleMessageSource getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundleMessageSource resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public String getMessage(String key){
		return getMessage(key, null, null, null);
	}
	
	public String getMessage(String key, Object[] args){
		return getMessage(key, args, null, null);
	}
	
	public String getMessage(String key, String defValue){
		return getMessage(key, null, defValue, null);
	}
	
	public String getMessage(String key, Object[] args, String defValue){
		return getMessage(key, args, defValue, null);
	}
	
	public String getMessage(String key, Object[] args, String defValue, Locale lcl){
		if (lcl == null){
			lcl = Locale.getDefault();
		}
		
		if (resourceBundle != null){
			return resourceBundle.getMessage(key, args, defValue, lcl);
		}else{
			return key;
		}
	}
	
}


