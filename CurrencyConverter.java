import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CurrencyConverter {

  private static final String API_KEY = "3d2dd5a8d0321f8b690b7752";
  private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
  private static final Map<Integer, String> currencyMap = new HashMap<>();

  static {
    currencyMap.put(1, "ARS");
    currencyMap.put(2, "MXN");
    currencyMap.put(3, "BRL");
    currencyMap.put(4, "CLP");
    currencyMap.put(5, "COP");
    currencyMap.put(6, "USD");
    currencyMap.put(7, "EUR");
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Seleccione la moneda de origen:");
    printCurrencyMenu();
    int fromCurrencyChoice = scanner.nextInt();
    String fromCurrency = currencyMap.get(fromCurrencyChoice);

    System.out.println("Seleccione la moneda de destino:");
    printCurrencyMenu();
    int toCurrencyChoice = scanner.nextInt();
    String toCurrency = currencyMap.get(toCurrencyChoice);

    System.out.print("Introduce la cantidad a convertir: ");
    double amount = scanner.nextDouble();

    try {
      ExchangeRateResponse exchangeRateResponse = convertCurrency(fromCurrency, toCurrency, amount);
      double conversionResult = exchangeRateResponse.getConversionResult();
      System.out.println(String.format("%.2f %s es igual a %.2f %s", amount, fromCurrency, conversionResult,
              toCurrency));
      printConversionDetails(exchangeRateResponse);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void printCurrencyMenu() {
    System.out.println("1 - ARS - Peso argentino");
    System.out.println("2 - MXN - Peso mexicano");
    System.out.println("3 - BRL - Real brasileño");
    System.out.println("4 - CLP - Peso chileno");
    System.out.println("5 - COP - Peso colombiano");
    System.out.println("6 - USD - Dólar estadounidense");
    System.out.println("7 - EUR - Euro");
    System.out.print("Seleccione una opción: ");
  }

  public static ExchangeRateResponse convertCurrency(String fromCurrency, String toCurrency, double amount) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    String url = BASE_URL + API_KEY + "/pair/" + fromCurrency + "/" + toCurrency + "/" + amount;

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IOException("Unexpected code " + response.statusCode());
    }

    String responseData = response.body();
    Gson gson = new Gson();
    return gson.fromJson(responseData, ExchangeRateResponse.class);
  }

  public static void printConversionDetails(ExchangeRateResponse exchangeRateResponse) {
    System.out.println("Tasa de conversión: " + exchangeRateResponse.getConversionRate());
    System.out.println("Última actualización: " + exchangeRateResponse.getLastUpdateTime());
    System.out.println("Próxima actualización: " + exchangeRateResponse.getNextUpdateTime());
  }

  static class ExchangeRateResponse {
    @SerializedName("conversion_rate")
    private double conversionRate;

    @SerializedName("time_last_update_utc")
    private String lastUpdateTime;

    @SerializedName("time_next_update_utc")
    private String nextUpdateTime;

    @SerializedName("conversion_result")
    private double conversionResult;

    public double getConversionRate() {
      return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
      this.conversionRate = conversionRate;
    }

    public String getLastUpdateTime() {
      return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
    }

    public String getNextUpdateTime() {
      return nextUpdateTime;
    }

    public void setNextUpdateTime(String nextUpdateTime) {
      this.nextUpdateTime = nextUpdateTime;
    }

    public double getConversionResult() {
      return conversionResult;
    }

    public void setConversionResult(double conversionResult) {
      this.conversionResult = conversionResult;
    }
  }
};