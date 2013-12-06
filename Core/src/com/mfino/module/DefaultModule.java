/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.module;

import com.google.inject.AbstractModule;

/**
 *
 * @author xchen
 * This default module will provide the direct mappings between class and instances
 */
public class DefaultModule extends AbstractModule {

    @Override
    protected void configure() {
    }

//    @Provides
//    CommodityTransferDAO provideCommodityTransferDAO() {
//        return new CommodityTransferDAO();
//    }
}
