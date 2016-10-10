

package com.mfino.integrations.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationPartnerMappingDAO;
import com.mfino.domain.IpMapping;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.fix.CmFinoFIX;
import com.mfino.integrations.vo.IntegrationDetails;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
public class IntegrationService{

	public static XMLResult validateIntegration(IntegrationDetails integrationDetails)
	{
		XMLResult result = new XMLResult(){
			public void render() throws Exception {
				writeStartOfDocument();
				super.render();
				writeEndOfDocument();
			}
		};
		
		IntegrationPartnerMappingDAO integrationPartnerMappingDao = DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
		IntegrationPartnerMap integrationPartnerMapping = integrationPartnerMappingDao.getByInstitutionID(integrationDetails.getInstitutionID());
		if(integrationPartnerMapping == null)
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidInstituionID);
			return result;
		}
		result.setIntegrationName(integrationPartnerMapping.getIntegrationname());
		
		//String integrationName = integrationPartnerMapping.getIntegrationName();
		//if(!integrationName.equals(integrationDetails.getIntegrationName()))
		//{
		//	result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidIntegrationName);
		//	return result;
		//}
		Set<IpMapping> ipMappings = integrationPartnerMapping.getIpMappings();
		Iterator<IpMapping> it = ipMappings.iterator();
		boolean ipFound = false;
		IpMapping ipMapping = null;
		while(it.hasNext())
		{
			ipMapping = it.next();
			String ipAddress = ipMapping.getIpaddress();
			try {
				if(ipAddressMatches(integrationDetails, ipAddress))
				{
					ipFound = true;
					break;
				}
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidIPAddress);
				return result;
			}	
		}
		if(!ipFound) 
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidIPAddress);
			return result;			
		}

		if(integrationPartnerMapping.getIsauthenticationkeyenabled() != null && 
				integrationPartnerMapping.getIsauthenticationkeyenabled() != 0)
		{
			String storedAuthenticationKey = integrationPartnerMapping.getAuthenticationkey();
			String authenticationKey = integrationDetails.getAuthenticationKey();
			String digestedCode = MfinoUtil.calculateDigestPin(integrationPartnerMapping.getInstitutionid(), authenticationKey);
			if(!digestedCode.equals(storedAuthenticationKey))
			{
				result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_Authentication_Failed);
				return result;
			}				
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_Validation_Successful);
		return result;	
	}

	
	public static boolean isLoginEnabledForIntegration(IntegrationDetails integrationDetails)
	{
		IntegrationPartnerMap integrationPartnerMapping =  DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getByInstitutionID(integrationDetails.getInstitutionID());
		return (integrationPartnerMapping.getIsloginenabled() != null && integrationPartnerMapping.getIsloginenabled() != 0);		
	}
	
	public static boolean ipAddressMatches(IntegrationDetails integrationDetails, String ipAddress) throws UnknownHostException
	{
		
		String[] ipAddressParts = integrationDetails.getIPAddress().split("\\.", 4);
		if(InetAddress.getByName(ipAddress).equals(InetAddress.getByName(integrationDetails.getIPAddress())))
			return true;
		if(ipAddress.matches("^0.0.0.0$"))
		{
			return true;
		}
		else if(ipAddress.matches("(.*)\\.0.0.0$"))
		{
			if(ipAddress.startsWith(ipAddressParts[0]))
				return true;
		}
		else if(ipAddress.matches(".*\\.0.0$"))
		{
			if(ipAddress.startsWith(ipAddressParts[0].concat(".").concat(ipAddressParts[1])))
				return true;
		}
		else if(ipAddress.matches(".*\\.0$"))
		{
			if(ipAddress.startsWith(ipAddressParts[0].concat(".").concat(ipAddressParts[1]).concat(".").concat(ipAddressParts[2])))
				return true;
		}
		
		return false;		
	}
}
