package prashakar.pricingbrowser;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by prash on 15/11/16.
 */

public class GetBitcoinData extends AsyncTask<URL, Void, Float>{

    public AsyncResponse listener;

    public GetBitcoinData(AsyncResponse listener){
        this.listener = listener;
    }
    @Override
    protected Float doInBackground(URL... urls) {
        Float bitcoinValue = 0f;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) urls[0].openConnection();

            int result = httpURLConnection.getResponseCode();

            if(result == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                bitcoinValue = Float.valueOf(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitcoinValue;
    }

    @Override
    protected void onPostExecute(Float result){
        listener.onProcessFinish(result);
    }
}
