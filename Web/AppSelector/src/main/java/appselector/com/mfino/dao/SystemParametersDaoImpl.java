package appselector.com.mfino.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;
 
import appselector.com.mfino.domain.AppDetails;

/**
 * 
 * @author Shashank
 *
 */
@Repository("SystemParametersDaoImpl")
public class SystemParametersDaoImpl extends SimpleJdbcDaoSupport implements SystemParametersDao {

     protected final Log log = LogFactory.getLog(getClass());
     public AppDetails getAppDetails(String ParameterName) {
     
    	 log.info("Getting appDetails !!");
        List<AppDetails> appDetailsList = new ArrayList<AppDetails>();
        String sqlQuery = "select ID, ParameterName, ParameterValue from system_parameters where ParameterName=? ;";
        AppDetails appDetails=null;
        appDetailsList=  getSimpleJdbcTemplate().query(sqlQuery, new ProductMapper(), ParameterName);
     
        for (AppDetails appDetailsIterator : appDetailsList) {
        	if(null!=appDetailsIterator.getParameterName()||null!= appDetailsIterator.getParameterValue()){
        		appDetails =appDetailsIterator;
        	}
          }
          return appDetails;
    }

    public void saveAppDetails(AppDetails app) {
      
    	String sql = "update system_parameters set  ParameterName= :ParameterName, ParameterValue = :ParameterValue where id = :id";
    	int count = getSimpleJdbcTemplate().update(sql ,new MapSqlParameterSource()
    			.addValue("ParameterName", app.getParameterName())
                .addValue("ParameterValue", app.getParameterValue())
                .addValue("id", app.getId())); 
    	log.debug("number of coloumns changed "+count);
      }
    

}
