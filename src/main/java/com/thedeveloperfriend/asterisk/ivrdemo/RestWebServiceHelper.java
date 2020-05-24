package com.thedeveloperfriend.asterisk.ivrdemo;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

//Helper class to call REST web services
public class RestWebServiceHelper {

	// get the stock price by calling a online demo REST web service
	// (https://thedeveloperfriend.com/stock_price_rest_ws/GetStockPrice.php?StockSymbol=ORCL)
	public double getStockPrice(String stockSymbol) {

		Client client = Client.create();
		WebResource webResource = client.resource(
				"https://thedeveloperfriend.com/stock_price_rest_ws/GetStockPrice.php?StockSymbol=" + stockSymbol);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

		String output = response.getEntity(String.class);
		JSONObject jsonObject = new JSONObject(output);
		String symbol = jsonObject.getString("StockSymbol");
		double price = jsonObject.getDouble("Price");

		return price;

	}

}
