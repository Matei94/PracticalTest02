package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matei on 15-May-16.
 */
public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (serverThread == null || socket == null) {
            Log.d(Constants.TAG, "serverThread or socket is null");
            return;
        }

        try {
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(this.socket.getOutputStream(), true);

            if (socketReader == null || socketWriter == null) {
                Log.d(Constants.TAG, "socket reader or writer is null");
            } else {
                String city = socketReader.readLine();
                String informationType = socketReader.readLine();

                if (city == null || city.isEmpty() ||
                        informationType == null || informationType.isEmpty()) {
                    Log.d(Constants.TAG, "city or information type is empty or null");
                } else {
                    HashMap<String, WeatherForecastInformation> data = serverThread.getData();
                    WeatherForecastInformation weatherForecastInformation = null;
                    if (data.containsKey(city)) {
                        weatherForecastInformation = data.get(city);
                    } else {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair(Constants.QUERY, city));
                        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        httpPost.setEntity(urlEncodedFormEntity);
//                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                        String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String pageSourceCode = null;
                        if (httpEntity == null) {
                            Log.d(Constants.TAG, "entity is null");
                        } else {
                            pageSourceCode = EntityUtils.toString(httpEntity);
                            Log.d(Constants.TAG, "page: " + pageSourceCode);
                        }
                        if (pageSourceCode == null) {
                            Log.d(Constants.TAG, "page source code is null");
                        } else {
                            Document document = Jsoup.parse(pageSourceCode);
                            Element element = document.child(0);
                            Elements scripts = element.getElementsByTag(Constants.SCRIPT_TAG);
                            for (Element script : scripts) {
                                String scriptData = script.data();
                                if (scriptData.contains(Constants.SEARCH_KEY)) {
                                    int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                                    scriptData = scriptData.substring(position);
                                    JSONObject content = new JSONObject(scriptData);
                                    JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);

                                    String temperature = currentObservation.getString(Constants.TEMPERATURE);
                                    String windSpeed   = currentObservation.getString(Constants.WIND_SPEED);
                                    String condition   = currentObservation.getString(Constants.CONDITION);
                                    String pressure    = currentObservation.getString(Constants.PRESSURE);
                                    String humidity    = currentObservation.getString(Constants.HUMIDITY);

                                    weatherForecastInformation = new WeatherForecastInformation(
                                            temperature,
                                            windSpeed,
                                            condition,
                                            pressure,
                                            humidity
                                    );

                                    serverThread.setData(city, weatherForecastInformation);
                                    break;
                                }
                            }
                        }
                    }

                    if (weatherForecastInformation == null) {
                        Log.d(Constants.TAG, "No weather info available");
                    } else {
                        String result = null;
                        if (Constants.ALL.equals(informationType)) {
                            result = weatherForecastInformation.toString();
                        } else if (Constants.TEMPERATURE.equals(informationType)) {
                            result = weatherForecastInformation.getTemperature();
                        } else if (Constants.WIND_SPEED.equals(informationType)) {
                            result = weatherForecastInformation.getWindSpeed();
                        } else if (Constants.CONDITION.equals(informationType)) {
                            result = weatherForecastInformation.getCondition();
                        } else if (Constants.HUMIDITY.equals(informationType)) {
                            result = weatherForecastInformation.getHumidity();
                        } else if (Constants.PRESSURE.equals(informationType)) {
                            result = weatherForecastInformation.getPressure();
                        } else {
                            result = "Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
                        }

                        socketWriter.println(result);
                        socketWriter.flush();
                    }
                }
            }

            socket.close();
        } catch (Exception e) {
            Log.d(Constants.TAG, e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
