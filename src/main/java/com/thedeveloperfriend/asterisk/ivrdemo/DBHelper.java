package com.thedeveloperfriend.asterisk.ivrdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Database helper class for IVR program
public class DBHelper {

	private static String HOST_NAME = "localhost";
	private static String PORT = "3306";
	private static String DATABASENAME = "stock_db";
	private static String USERNAME = "stock_db_user";
	private static String PASSWORD = "weblogic";

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DATABASENAME,
				USERNAME, PASSWORD);
		return connection;
	}

	private void closeDBObjects(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection)
			throws SQLException {
		resultSet.close();
		preparedStatement.close();
		connection.close();
	}
	
	public boolean validateUsernamePassword(String user_id, String password)
			throws ClassNotFoundException, SQLException {
		boolean isValidCredentials = false;
		Connection connection = getConnection();
		String sqlQuery = "select * from customer where user_id=? and password=?";
		PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
		preparedStatement.setString(1, user_id);
		preparedStatement.setString(2, password);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			isValidCredentials = true;
		}
		
		closeDBObjects(resultSet, preparedStatement, connection);

		return isValidCredentials;
	}

}
