/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.services;

import com.mfino.tools.schemachangeupgradetool.constants.SchemaChangeUpgradeConstants;
import com.mfino.tools.schemachangeupgradetool.dao.DbParamDAO;
import com.mfino.domain.DbParam;
import com.mfino.util.logging.DefaultLogger;

/**
 *
 * @author sandeepjs
 */
public class DbParamService {

    private DbParamDAO dbPDao = new DbParamDAO();

    public void createVersionRecord()
    {
        DbParam dbParam = new DbParam();
        dbParam.setName( SchemaChangeUpgradeConstants.NAME_VERSION);
        dbParam.setValue(SchemaChangeUpgradeConstants.INITIAL_DB_VERSION);
        dbPDao.saveDbParam(dbParam);
    }

    public void updateDbVersion(String newVersion)
    {
        DbParam dbParam = dbPDao.getDbParam(SchemaChangeUpgradeConstants.NAME_VERSION);
        if(dbParam != null)
        {
        dbParam.setValue(newVersion);
        dbPDao.saveDbParam(dbParam);
        }
    }

    public int getDbVersion()
    {
        DbParam dbParam = dbPDao.getDbParam(SchemaChangeUpgradeConstants.NAME_VERSION);
       
        if(dbParam != null)
        {
            return Integer.parseInt(dbParam.getValue());
        }
        else
        {
            return -1;
        }
    }

}
