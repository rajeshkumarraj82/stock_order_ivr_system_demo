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

	// This method will be invoked whenever a user calls the extension 9999
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			// Answer the incoming call
			answer();
			this.logger.debug("CALL ANSWERED ......");

			// Get the caller number. This is the User Id
			String userId = request.getCallerId();
			this.logger.debug("userId = " + userId);

			// Get 6 digit password from the user
			String password = getPasswordFromUser();

			// Terminate the call
			hangup();
			this.logger.debug("CALL HANGUP ......");
		} catch (UserReTryCountExceededException e) {
			// Stream the audio for user exceeded all the retry options
			streamFile("digits/0");

			// Terminate the call since the user exceeded all the retry options
			hangup();
			this.logger.debug("CALL HANGUP ......");

		}
	}

	// This method will receive and validate the six digit password from user.
	// If password is empty or invalid it will prompt the user to reenter for
	// maximum 3 times
	public String getPasswordFromUser() throws AgiException, UserReTryCountExceededException {
		String password = null;
		try {
			int retry_counter = 0;
			while (true) {
				password = getData("digits/1", 15000, 6);
				this.logger.debug("password = " + password);

				if (password != null && password.length() == 6 && StringUtils.isNumeric(password)) {
					return password;
				} else if (retry_counter > 2) {
					throw new UserReTryCountExceededException("User exceeded all retry options when typing password");
				}
				retry_counter++;
			}

		} catch (UserReTryCountExceededException e) {
			throw e;
		}

	}

}
