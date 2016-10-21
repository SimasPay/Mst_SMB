/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SecurityConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;

/**
 *
 * @author Siddhartha Chinthapally
 *
 */
public class CustomUserDetailsService implements UserDetailsService {
    private String appName;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userService;
     
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
    public UserDetails loadUserByUsername(String username) {
        List<MfinoUser> users;
        MfinoUser user;
        UserDetailsServiceImpl userAdapter = null;
        try {
            UserQuery query = new UserQuery();
            query.setUserName(username);
            if(StringUtils.isNotBlank(getAppName()) && "creditcardpayment".equals(getAppName().toLowerCase())) {
                query.setRole(CmFinoFIX.Role_Subscriber);
            }
            else
            {
                query.setNotequalsRole(CmFinoFIX.Role_Subscriber);
            }
            UserDAO userdao = DAOFactory.getInstance().getUserDAO();
            users = userdao.get(query);
            if (null == users || 0 == users.size()) {
                //throw new UsernameNotFoundException("User " + username + " does not exist");
            /* Don't let the end user know that the username doesn't exist
                 * But we need to distinguish between invalid username and incorrect password to
                 * correctly implement the automatic security lock
                 */
                throw new UsernameNotFoundException(MessageText._("Invalid username or password"));
            }

            user = users.get(0);
            userService.loadPermissions(user);
            if (user.getFailedlogincount() == 0) {
                user.setFailedlogincount(0);
            }
            
            if (user.getFailedlogincount() >= SecurityConstants.MAX_LOGIN_TRIES) {
                Integer restr = user.getRestrictions();
                if (restr == null) {
                    restr = 0;
                }
                restr += CmFinoFIX.SubscriberRestrictions_SecurityLocked;
                user.setRestrictions(restr);
                //reset the failed login count
                user.setFailedlogincount(0);
                userdao.save(user);
            }

            userAdapter = new UserDetailsServiceImpl(user);
        } catch (Exception e) {
            if (e instanceof UsernameNotFoundException) {
                throw (UsernameNotFoundException)e;
            } else {
                log.error("Error getting user " + username, e);
                throw new DataRetrievalFailureException("error getting user", e);
            }
        }

        return userAdapter;
    }
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppName() {
		return appName;
	}        
}
