package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //szöveges mező előkészítése
        TextView szovegTextView = findViewById(R.id.szoveg);
        szovegTextView.setText("Nyomja le a gombot!");

        //nyomógomb előkészítése
        Button gomb1 = findViewById(R.id.gomb1);
        gomb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                szovegTextView.setText("LEKÉRÉS…");
                //URL lekérése, a lenti csak példa
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        halozat adatlekeres = new halozat();
                        try {
                            String jsonVisszajelzes = adatlekeres.lekeres("https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=IDEADDMEGAKULCSOD&units=metric");
                            Gson gson = new Gson();
                            JsonObject jsonObj = gson.fromJson(jsonVisszajelzes, JsonObject.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hőmérséklet kiolvasása
                                    float temperature = jsonObj.getAsJsonObject("main").get("temp").getAsFloat();
                                    System.out.println("Hőmérséklet: " + temperature);
                                    szovegTextView.setText("Hőmérséklet: " + temperature + " °C");
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}

//URL meghívása és a visszaadott
//kimenet összegyűjtése
class halozat {
    public String lekeres(String url) throws IOException {
        StringBuilder tartalom = new StringBuilder();

        //távoli kapcsolat felépítése és megnyitása
        System.out.println("open connection...");
        //val httpURLConnection: HttpURLConnection = _url.openConnection() as HttpURLConnection
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        System.out.println("getresp code...");
        int responseCode = httpURLConnection.getResponseCode();

        System.out.println("kezdes...");
        System.out.println("resp code:" + Integer.toString(responseCode));

        //kapcsolat ellenőrzése
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("http ok...");
            //kimenet visszaolvasásának előkészítése
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //kimenet összerakása
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tartalom.append(line);
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }
        }

        //kapcsolat bontása
        httpURLConnection.disconnect();
        return tartalom.toString();
    }
}
