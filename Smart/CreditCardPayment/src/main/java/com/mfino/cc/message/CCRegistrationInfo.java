/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.cc.message;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mfino.domain.CreditCardDestinations;

/**
 *
 * @author admin
 */
public class CCRegistrationInfo {

    private String firstName=StringUtils.EMPTY;
    private String lastName=StringUtils.EMPTY;
    private String securityQuestion;
    private String securityAnswer;
    private String password;
    private String email;
    private Long userid;
    private Long subscriberid;
    private String mdn;
    private Integer userVersion;
    private Integer subscriberVersion;
    private Date dateOfBirth;
    private Long companyid;
    private Integer errorCode;
    private String errorDescription;
    private String homePhone;
    private String workPhone;
    private Boolean isConfirmationRequired;
    private List<CCInfo> ccList;
    private List<CreditCardDestinations> ccDestinations;
    private String username;
    private Integer newDestinations;
    private Integer oldDestinations;
    private Map<String,Long> ccDestCompIDs;
    
    public Long getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Long companyid) {
        this.companyid = companyid;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public List<CCInfo> getCcList() {
        return ccList;
    }

    public void setCcList(List<CCInfo> ccList) {
        this.ccList = ccList;
    }
   

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public Integer getSubscriberVersion() {
        return subscriberVersion;
    }

    public void setSubscriberVersion(Integer subscriberVersion) {
        this.subscriberVersion = subscriberVersion;
    }

    public Long getSubscriberid() {
        return subscriberid;
    }

    public void setSubscriberid(Long subscriberid) {
        this.subscriberid = subscriberid;
    }

    public Integer getUserVersion() {
        return userVersion;
    }

    public void setUserVersion(Integer userVersion) {
        this.userVersion = userVersion;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

	@Override
	public String toString() {
		String destinationList="";
		if(ccDestinations!=null){
		Iterator<CreditCardDestinations> ccdest=ccDestinations.iterator();
				while(ccdest.hasNext()){
			destinationList+=ccdest.next().getDestMDN()+"  ";
		}
		}
		return "CCRegistrationInfo [firstName=" + firstName + ", lastName="
				+ lastName + ", securityQuestion=" + securityQuestion
				+ ", securityAnswer=" + securityAnswer + ", password="
				+ password + ", email=" + email + ", userid=" + userid
				+ ", subscriberid=" + subscriberid + ", mdn=" + mdn
				+ ", userVersion=" + userVersion + ", subscriberVersion="
				+ subscriberVersion + ", dateOfBirth=" + dateOfBirth
				+ ", companyid=" + companyid + ", errorCode=" + errorCode
				+ ", errorDescription=" + errorDescription + ", ccList="
				+ ccList + ", ccDestinationList=["+destinationList+"]";
	}

	public void setCcDestinations(List<CreditCardDestinations> ccDestinations) {
		this.ccDestinations = ccDestinations;
}

	public List<CreditCardDestinations> getCcDestinations() {
		return ccDestinations;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setIsConfirmationRequired(Boolean isConfirmationRequired) {
		this.isConfirmationRequired = isConfirmationRequired;
	}

	public Boolean getIsConfirmationRequired() {
		return isConfirmationRequired;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setNewDestinations(Integer newDestinations) {
		this.newDestinations = newDestinations;
	}

	public Integer getNewDestinations() {
		return newDestinations;
	}

	public void setOldDestinations(Integer oldDestinations) {
		this.oldDestinations = oldDestinations;
	}

	public Integer getOldDestinations() {
		return oldDestinations;
	}

	public void setCcDestCompIDs(Map<String, Long> ccDestCompIDs) {
		this.ccDestCompIDs = ccDestCompIDs;
}

	public Map<String, Long> getCcDestCompIDs() {
		return ccDestCompIDs;
	}
}
