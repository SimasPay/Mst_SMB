/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;

/**
 * @author Siddhartha Chinthapally
 */
public class AuthenticationAttemptListener implements ApplicationListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void onApplicationEvent(ApplicationEvent event) {
        try {

            if (event instanceof AuthenticationFailureBadCredentialsEvent) {
                AuthenticationFailureBadCredentialsEvent failEvent =
                        (AuthenticationFailureBadCredentialsEvent) event;

                Exception e = failEvent.getException();
                //It could be because username doesn't exists or bad password.
                //We are intersted in bad password only right now.
                if (e instanceof UsernameNotFoundException) {
                    //Do nothing
                } else if (e instanceof BadCredentialsException) {
                    //increment the failed attempts count.
                    String username = failEvent.getAuthentication().getName();
                    UserQuery query = new UserQuery();
                    query.setUserName(username);

                    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
                    List<User> list = userDAO.get(query);

                    if (list.size() > 0) {
                        User u = list.get(0);
                        int failedCount = (u.getFailedLoginCount() == null)
                                ? 1
                                : u.getFailedLoginCount() + 1;
                        u.setFailedLoginCount(failedCount);
                        userDAO.save(u);
                    }
                }
            } else if (event instanceof AuthenticationSuccessEvent) {
                AuthenticationSuccessEvent successEvent = (AuthenticationSuccessEvent) event;
                String username = successEvent.getAuthentication().getName();
                UserQuery query = new UserQuery();
                query.setUserName(username);

                UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
                List<User> list = userDAO.get(query);
                if (list.size() > 0) {
                    User u = list.get(0);
                    //reset the failed attempts count
                    if (u.getFailedLoginCount() > 0) {
                        u.setFailedLoginCount(0);
                        userDAO.save(u);
                    }
                }
            }
        } catch (Throwable ex) {
            log.error(this.getClass().getName(), ex);
        }
    }
}
