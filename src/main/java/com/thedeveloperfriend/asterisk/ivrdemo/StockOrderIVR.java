package com.thedeveloperfriend.asterisk.ivrdemo;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Asterisk AGI class implementing demo stock order IVR system
public class StockOrderIVR extends BaseAgiScript {
	Logger logger = LogManager.getRootLogger();
	DBHelper dbHelper = new DBHelper();
	RestWebServiceHelper restWebServiceHelper = new RestWebServiceHelper();

	// This method will be invoked whenever a user calls the extension 9999
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			// Answer the incoming call
			answer();
			this.logger.debug("CALL ANSWERED ......");

			// Get the caller number. This is the User Id
			String userId = request.getCallerId();
			this.logger.debug("userId = " + userId);

			// Get 6 digit password from the user and validate against database
			getAndValidatePassword(userId);

			// Ask the user to select a stock by pressing 1,2 or 3
			String selectedStockSymbol = getUserSelectedStockSymbol();
			this.logger.debug("userId = " + userId + " : selectedStockSymbol = " + selectedStockSymbol);

			// get the price of the selected stock by calling a REST web service.
			double stockPrice = restWebServiceHelper.getStockPrice(selectedStockSymbol);
			this.logger.debug("userId = " + userId + " : stockPrice = " + stockPrice);

			// get the quantity from user
			String quantity = getQuantity();
			this.logger.debug("userId = " + userId + " : quantity = " + quantity);

			// confirm the quantity with user
			if (confirmQuantity(quantity)) {
				this.logger.debug("userId = " + userId + " : User confirmed the quantity");
				// user confirmed the quantity so go ahead and place the order
				long orderId = dbHelper.insertOrderDetails(Integer.parseInt(userId), selectedStockSymbol,
						Integer.parseInt(quantity), stockPrice);
				// Play the audio "You have successfully placed the order. Your Order Id is"
				streamFile("success_msg");
				// Play the Order Id generated
				sayDigits(String.valueOf(orderId));

			} else {
				// user did't confirm the quantity
				streamFile("invalid_entry");
				hangup();
			}

			// Terminate the call
			hangup();
			this.logger.debug("CALL HANGUP ......");
		} catch (UserReTryCountExceededException e) {
			// Stream the audio for user exceeded all the retry options
			streamFile("retry_exceeded");

			// Terminate the call since the user exceeded all the retry options
			hangup();
			this.logger.debug("CALL HANGUP ......");

		} catch (Exception exception) {
			this.logger.error(exception);

		}
	}

	// This method will receive and validate the six digit password from user.
	// If password is empty or invalid it will prompt the user to reenter for
	// maximum 3 times
	public void getAndValidatePassword(String user_id) throws AgiException, UserReTryCountExceededException, Exception {
		String password = null;
		try {
			int retry_counter = 0;
			while (true) {
				// Play the audio "Please enter your six digit password"
				password = getData("ask_password", 15000, 6);
				this.logger.debug("password = " + password);

				if (password != null && password.length() == 6 && StringUtils.isNumeric(password)
						&& dbHelper.validateUsernamePassword(user_id, password)) {
					break;
				} else if ((retry_counter < 2)) {
					retry_counter++;
					// Play the audio "You have entered an invalid option"
					streamFile("invalid_entry");
					continue;
				} else {
					throw new UserReTryCountExceededException("User exceeded all retry options");
				}

			}

		} catch (UserReTryCountExceededException e) {
			throw e;
		} catch (Exception exception) {
			throw exception;
		}

	}

	// Method to ask the user to select his stock.
	// 1 for IBM, 2 for MSFT or 3 for ORCL
	public String getUserSelectedStockSymbol() throws UserReTryCountExceededException, AgiException {

		try {
			int retry_counter = 0;
			while (true) {
				// Play the audio "Please select your stock. Press 1 for IBM. Press 2 for
				// MicroSoft. or Press 3 for Oracle"
				String selectedOption = getData("select_stock", 15000, 1);
				this.logger.debug("selectedOption = " + selectedOption);

				// make sure the selectedOption is not null
				if (selectedOption == null) {
					selectedOption = "";
				}

				switch (selectedOption) {
				case "1":
					return "IBM";
				case "2":
					return "MSFT";
				case "3":
					return "ORCL";
				default:
					// Play the audio "You have entered an invalid option"
					streamFile("invalid_entry");
					if ((retry_counter < 2)) {
						retry_counter++;
						continue;
					} else {
						throw new UserReTryCountExceededException("User exceeded all retry options");
					}

				}

			}

		} catch (UserReTryCountExceededException e) {
			throw e;
		}
	}

	// Method to ask the user to enter the quantity followed by hash symbol
	// The maximum digits allowed is 4
	public String getQuantity() throws AgiException, UserReTryCountExceededException {

		try {
			int retry_counter = 0;
			while (true) {
				// Play the audio "Please enter the buy quantity"
				String qty = getData("enter_quantity", 15000, 4);
				this.logger.debug("quantity typed by user = " + qty);

				if (qty != null && StringUtils.isNumeric(qty)) {
					return qty;
				} else if ((retry_counter < 2)) {
					// Play the audio "You have entered an invalid option"
					streamFile("invalid_entry");
					retry_counter++;
					continue;
				} else {
					throw new UserReTryCountExceededException("User exceeded all retry options");
				}

			}

		} catch (UserReTryCountExceededException e) {
			throw e;
		}

	}

	// Method to confirm the quantity number entered
	public boolean confirmQuantity(String quantity) throws AgiException {
		boolean isCorrectQuantity = false;
		// Play the quantity to user
		streamFile("qty_repeat_msg");
		sayNumber(quantity);

		// Press 1 to confirm. Press 2 for cancel
		String confirmationCode = getData("ask_confirmation", 15000, 1);

		if (confirmationCode != null && confirmationCode.equals("1")) {
			isCorrectQuantity = true;
		}

		return isCorrectQuantity;
	}

}
