package com.danielstone.html;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class Controller implements Initializable{

    @FXML
    public static WebView webView;

    @FXML public void savePressed() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView = new WebView();
        WorkerTask task = new WorkerTask();
        task.execute();
    }

    public class WorkerTask extends SwingWorker<String, Integer> {
        @Override
        protected void done() {
            try {
                System.out.println(get());
                String result = get();
                JSONObject jsonObject = (JSONObject) JSONValue.parse(result);
                JSONObject cityObject = (JSONObject) jsonObject.get("city");
                JSONArray listArray = (JSONArray) jsonObject.get("list");
                JSONObject zeroObject = (JSONObject) listArray.get(0);
                JSONObject tempObject = (JSONObject) zeroObject.get("temp");
                System.out.println(tempObject.get("max"));

                String toPrint = cityObject.get("name") + "\n"
                        + tempObject.get("max") + " / " + tempObject.get("min")
                        + "\n" + "Pressure: " + zeroObject.get("pressure")
                        + "\n" + "Humidity: " + zeroObject.get("humidity");

                String html =
                        "<table><tr><td>Weather for " + tempObject.get("name") +
                                "</td></tr><tr><td><h1>" +
                                tempObject.get("max") +"°</td></h1></tr><tr><td><h2>" +
                                tempObject.get("min") +"°</td></h2></tr><tbody>";

                Platform.runLater(() -> {
                    Controller.webView.getEngine().loadContent(html);
                });

                System.out.println(html);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            super.done();
        }

        @Override
        protected String doInBackground() throws Exception {

            HttpURLConnection httpURLConnection = null;
            String result = "";

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=json&units=metric&cnt=7&APPID=9858e11d9d8a99516b8230dacdd9bb1d");
                //URL url = new URL("https://twitter.com/daniel_stoneuk");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {


                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);

                    int progress = 0;
                    int data = reader.read();
                    while (data != -1) {
                        char currentChar = (char) data;

                        result = result + currentChar;

                        progress ++;
                        publish(progress);

                        data = reader.read();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection == null) httpURLConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void process(List<Integer> chunks) {
            for (int i : chunks) {
                System.out.println(i);
                //Platform.runLater(() ->loadingLabel.setText("Loading: " + i + "%"));
            }
            super.process(chunks);
        }
    }
}
