package appselector.com.mfino.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import appselector.com.mfino.domain.AppDetails;

public class ProductMapper implements RowMapper<AppDetails> {

    public AppDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppDetails app = new AppDetails();
        app.setId(rs.getInt("id"));
        app.setParameterName(rs.getString("ParameterName"));
        app.setParameterValue(rs.getString("ParameterValue"));
        return app;
    }
}