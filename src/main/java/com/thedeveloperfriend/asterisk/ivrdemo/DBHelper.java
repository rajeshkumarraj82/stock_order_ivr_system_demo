package com.thedeveloperfriend.asterisk.ivrdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Database helper class for IVR program
public class DBHelper {

	Logger logger = LogManager.getRootLogger();

	private static String HOST_NAME = "localhost";
	private static String PORT = "3306";
	private static String DATABASENAME = "stock_db";
	private static String USERNAME = "stock_db_user";
	private static String PASSWORD = "Password@123";

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
		this.logger.debug(
				"DBHelper:validateUsernamePassword:user_id=" + user_id + ":isValidCredentials=" + isValidCredentials);
		return isValidCredentials;
	}

	public long insertOrderDetails(int user_id, String stock_symbol, int qty, double price)
			throws SQLException, ClassNotFoundException {
		long generatedOrderId = 0;
		Connection connection = getConnection();
		String sqlQuery = "insert into stock_order (user_id, stock_symbol, qty, price) values(?,?,?,?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery,
				PreparedStatement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, user_id);
		preparedStatement.setString(2, stock_symbol);
		preparedStatement.setInt(3, qty);
		preparedStatement.setDouble(4, price);
		preparedStatement.executeUpdate();

		ResultSet resultSet = preparedStatement.getGeneratedKeys();
		if (resultSet.next()) {
			generatedOrderId = resultSet.getLong(1);
		}

		return generatedOrderId;
	}

}
