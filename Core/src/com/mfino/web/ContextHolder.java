/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author xchen
 */
public class ContextHolder implements ApplicationContextAware {

    private static ApplicationContext appContext;
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public static ApplicationContext getCurrentApplicationContext(){
        return appContext;
    }

}
