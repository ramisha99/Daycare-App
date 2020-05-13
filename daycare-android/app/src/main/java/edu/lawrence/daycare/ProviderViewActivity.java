package edu.lawrence.daycare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static edu.lawrence.daycare.EditChildActivity.CHILD_ID;
import static edu.lawrence.daycare.EditChildActivity.PARENT_ID;

public class ProviderViewActivity extends AppCompatActivity {

    public static final String PROVIDER_ID = "edu.lawrence.daycare.PROVIDER_ID";
    public static final String START_DATE = "edu.lawrence.daycare.START_DATE";
    public static final String END_DATE = "edu.lawrence.daycare.END_DATE";
    public int childId;
    public int providerId;
    public int parentId;
    public Date start;
    public Date end;
    Gson gson;
    SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        setContentView(R.layout.activity_provider_view);

        parentId = getIntent().getIntExtra(PARENT_ID, -1);
        childId = getIntent().getIntExtra(CHILD_ID, -1);
        providerId = getIntent().getIntExtra(PROVIDER_ID, -1);
        Log.d("ProviderViewActivity", getIntent().getStringExtra(START_DATE));
        try {
            start = textFormat.parse(getIntent().getStringExtra(START_DATE));
            end = textFormat.parse(getIntent().getStringExtra(END_DATE));
        } catch (Exception ex) { Log.d("oh no", "oh no"); finish(); }

        new RetrieveProviderTask(providerId).execute();
    }

    public void register(View v) {
        new CreateRegistrationTask(childId, providerId, start, end).execute();
    }

    private void setTextViewContents(int id, String text) {
        ((TextView)findViewById(id)).setText(text);
    }

    private void loadFromProvider(Provider p) {
        setTextViewContents(R.id.nameText, p.name);
        setTextViewContents(R.id.addressText, p.address);
        setTextViewContents(R.id.cityText, p.city);
        setTextViewContents(R.id.minAgeText, p.minAge);
        setTextViewContents(R.id.maxAgeText, p.maxAge);
    }

    private class RetrieveProviderTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveProviderTask(int id) {
            uri = "http://" + URIHandler.hostName + "/provider?id=" + id;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            loadFromProvider(gson.fromJson(result, Provider.class));
        }
    }

    private class CreateRegistrationTask extends AsyncTask<Void, Void, String> {
        private String uri;
        private String json;
        CreateRegistrationTask (int childId, int providerId, Date start, Date end) {
            uri = "http://" + URIHandler.hostName + "/registration";

            Registration r = new Registration();
            r.childId = childId;
            r.providerId = providerId;
            r.start = start;
            r.end = end;

            json = gson.toJson(r);
        }

        private Date fixDate(Date d) {
            return new Date(d.getTime() + 24 * 60 * 60 * 1000);
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doPost(uri, json);
        }

        @Override
        protected void onPostExecute(String result) {
            //loadFromProvider(gson.fromJson(result, Provider.class));
            finish();
        }
    }
}
