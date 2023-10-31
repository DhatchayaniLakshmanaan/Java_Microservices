package aplacabuy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class alpacabuy {

    // API keys and base URL for Alpaca API
    private static final String ALPACA_API_KEY = "PKBAIZAFKXUNUQ8CAP27";
    private static final String ALPACA_SECRET_KEY = "yF4rZ6K3EjGhwDhNiRo3pw8hnwV4SZ58bhBegFiN";
    private static final String ALPACA_BASE_URL = "https://paper-api.alpaca.markets"; // Use 'https://api.alpaca.markets' for live trading

    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Retrieve stock information for a given symbol
    public static JsonNode retrieveStockInfo(String symbol) throws Exception {
        String apiUrl = ALPACA_BASE_URL + "/v2/assets/" + symbol;
        HttpGet request = new HttpGet(apiUrl);

        // Set API credentials in the HTTP request headers
        request.setHeader("APCA-API-KEY-ID", ALPACA_API_KEY);
        request.setHeader("APCA-API-SECRET-KEY", ALPACA_SECRET_KEY);

        HttpResponse response = httpClient.execute(request);
        String json = EntityUtils.toString(response.getEntity());

        // Parse and return the JSON response as a JsonNode
        return objectMapper.readTree(json);
    }

    // Place a buy order for a given symbol and quantity
    public static void placeBuyOrder(String symbol, int quantity) throws Exception {
        String apiUrl = ALPACA_BASE_URL + "/v2/orders";
        HttpPost request = new HttpPost(apiUrl);

        // Set API credentials in the HTTP request headers
        request.setHeader("APCA-API-KEY-ID", ALPACA_API_KEY);
        request.setHeader("APCA-API-SECRET-KEY", ALPACA_SECRET_KEY);

        // Define the buy order JSON payload
        String orderJson = "{" +
                "\"symbol\":\"" + symbol + "\"," +
                "\"qty\":" + quantity + "," +
                "\"side\":\"buy\"," +
                "\"type\":\"market\"," +
                "\"time_in_force\":\"gtc\"" +
                "}";
        StringEntity entity = new StringEntity(orderJson);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = httpClient.execute(request);
        String responseJson = EntityUtils.toString(response.getEntity);

        // Print the response to the console
        System.out.println("Buy order response: " + responseJson);
    }

    public static void main(String[] args) {
        System.out.println("Alpaca API Console Application - Buy Stocks");

        while (true) {
            try {
                System.out.print("\nEnter a stock symbol (e.g., AAPL): ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String symbol = reader.readLine();

                if (symbol.equalsIgnoreCase("EXIT")) {
                    System.out.println("Exiting...");
                    break;
                }

                JsonNode stockInfo = retrieveStockInfo(symbol);

                if (stockInfo.has("symbol")) {
                    System.out.println("\nStock Information:");
                    System.out.println("Symbol: " + stockInfo.get("symbol").asText());
                    System.out.println("Name: " + stockInfo.get("name").asText());
                    System.out.println("Exchange: " + stockInfo.get("exchange").asText());
                    System.out.println("Tradable: " + (stockInfo.get("tradable").asBoolean() ? "Yes" : "No"));
                    System.out.println("Status: " + stockInfo.get("status").asText());
                } else {
                    System.out.println("Stock symbol '" + symbol + "' not found or an error occurred.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
