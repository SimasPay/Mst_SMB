package com.dimo.fuse.reports.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 * 
 */
public class QueryExecutor {
	static final String DB_URL = DBProperties.getDBUrl();
	static final String DB_DRIVERS = DBProperties.getJDBCDriver();
	static final String USER = DBProperties.getDBUserName();
	static final String PASSWORD = DBProperties.getDBPassword();

	private Connection connection = null;
	private PreparedStatement stmt = null;
	private ResultSet resultSet = null;
	private JSONArray encryptedFields;
	private String nullValueReplacementText = "";

	private static Logger log = LoggerFactory.getLogger(QueryExecutor.class);

	public QueryExecutor() {
		getConnection();
	}

	protected void finalize() {
		closeConnection();
	}

	public void getConnection() {
		log.trace("Connecting to database...");
		try {
			Class.forName(DB_DRIVERS);
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (Exception e) {
			log.error("An error occurred while getting db connection. "	+ e.getMessage());
			log.info("Attempting to reconnect to db...");
			getConnection();
			
		}
	}

	public void closeConnection() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("An exception occurred while clsoing the db connection. "
					+ e.getMessage());
		} finally {

		}
	}

	public ResultSet getResultSet(String query) {
		try {
			log.trace("Creating statement...");

			stmt = connection.prepareStatement(query,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			//stmt.setFetchSize(Integer.MIN_VALUE);
			resultSet = stmt.executeQuery();

		} catch (SQLException e) {
			log.error("An error occurred while executing the query. "
					+ e.getMessage());
		}
		return resultSet;

	}

	public String[] fetchNextRowData(ResultSet resultSet) {
		try {
			String[] rowContent = new String[resultSet.getMetaData()
					.getColumnCount()];
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				rowContent[i - 1] = (resultSet.getString(i) != null) ? resultSet
						.getString(i) : nullValueReplacementText;
			}

			if (encryptedFields != null) {
				for (int j = 0; j < encryptedFields.length(); j++) {
					try {
						rowContent[encryptedFields.getInt(j) - 1] = StringEncryptorDecryptor
								.decrypt(rowContent[encryptedFields.getInt(j) - 1]);
					} catch (EncryptionOperationNotPossibleException e) {
						log.warn("Invalid String. Either the string is not encrypted properly or is encrypted with different algorithm and/or key");
					} catch (JSONException e) {
						log.error("Error Occured while reading the JSON Array. Integer Array is expected.");
					}
				}
			}
			return rowContent;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void setEncryptedFields(JSONArray encryptedFields) {
		this.encryptedFields = encryptedFields;
	}

	public void setNullValueReplacementText(String nullValueReplacementText) {
		this.nullValueReplacementText = nullValueReplacementText;
	}
}