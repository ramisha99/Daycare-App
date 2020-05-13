package edu.lawrence.daycare;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static edu.lawrence.daycare.EditChildActivity.PARENT_ID;
import static edu.lawrence.daycare.ProviderViewActivity.END_DATE;
import static edu.lawrence.daycare.ProviderViewActivity.PROVIDER_ID;
import static edu.lawrence.daycare.ProviderViewActivity.START_DATE;

public class ProviderListActivity extends AppCompatActivity {

    Gson gson;
    SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<Child> children;
    int parentId;
    int selectedChildId;
    public Date startDate = new Date();
    public Date endDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        parentId = getIntent().getIntExtra(PARENT_ID, -1);
        setContentView(R.layout.activity_provider_list);
        ((ListView)findViewById(R.id.providerList)).setOnItemClickListener((p, v, pos, id) -> {
            Provider provider = (Provider)p.getItemAtPosition(pos);
            showProvider(provider.id);
        });
        update();
    }

    private void showProvider(int providerId) {
        Intent intent = new Intent(this, ProviderViewActivity.class);
        intent.putExtra(PARENT_ID, parentId);
        intent.putExtra(EditChildActivity.CHILD_ID, selectedChildId);
        intent.putExtra(PROVIDER_ID, providerId);
        intent.putExtra(START_DATE, textFormat.format(startDate));
        intent.putExtra(END_DATE, textFormat.format(endDate));
        startActivity(intent);
    }

    private void update() {
        new RetrieveChildrenTask(parentId).execute();

        setTextFieldText(R.id.startDateView, textFormat.format(startDate));
        setTextFieldText(R.id.endDateView, textFormat.format(endDate));
    }
    private void setTextFieldText(int id, String text) {
        ((TextView)findViewById(id)).setText(text);
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void setStartDate(View v) {
        DialogFragment newFragment = new DatePickerFragment(true);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setEndDate(View v) {
        DialogFragment newFragment = new DatePickerFragment(false);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

private class RetrieveChildrenTask extends AsyncTask<Void, Void, String> {
    private String uri;

    RetrieveChildrenTask(int id) {
        uri = "http://" + URIHandler.hostName + "/children?parent=" + id;
    }

    @Override
    protected String doInBackground(Void... ignored) {
        return URIHandler.doGet(uri, "");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(String result) {
        children = Arrays.asList(gson.fromJson(result, Child[].class));
        String[] names = new String[children.size()];

        for (int i = 0; i < names.length; i++)
            names[i] = children.get(i).name;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, names);
        Spinner spinner = findViewById(R.id.childSpinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Child child = children.get(position);
                selectedChildId = child.childId;
                new RetrieveMatchingProvidersTask(child.childId).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }
}

    private class RetrieveMatchingProvidersTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveMatchingProvidersTask (int childId) {
            uri = "http://" + URIHandler.hostName + "/child?id=" + childId;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            Child child = gson.fromJson(result, Child.class);

            if (child == null)
                return;

            Calendar c = Calendar.getInstance();
            Log.d("ProviderListActivity", Integer.toString(c.get(Calendar.MILLISECOND)));
            c.add(Calendar.MILLISECOND, (int)-child.birthDate.getTime());

            Log.d("ProviderListActivity", Integer.toString((int)((new Date().getTime() - child.birthDate.getTime()) / (24d * 30 * 60 * 60 * 1000))));

            new RetrieveProvidersTask((int)((new Date().getTime() - child.birthDate.getTime()) / (24d * 30 * 60 * 60 * 1000))).execute();
        }
    }

    private class RetrieveProvidersTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveProvidersTask(int age) {
            uri = "http://" + URIHandler.hostName + "/providers/by_child?age=" + age +
                    "&start=" + textFormat.format(startDate) +
                    "&end=" + textFormat.format(endDate);
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            ProviderAdapter adapter = new ProviderAdapter (gson.fromJson(result, Provider[].class));
            ListView list = findViewById(R.id.providerList);
            list.setAdapter(adapter);
        }
    }

    public class ProviderAdapter extends BaseAdapter {
        List<Provider> providers;

        public ProviderAdapter(Provider[] providers) {
            this.providers = Arrays.asList(providers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position).name);
            return convertView;
        }

        @Override
        public int getCount() {
            return providers.size();
        }

        @Override
        public Provider getItem(int position) {
            return providers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id; // assuming all received IDs are unique
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean setStartDate = false;

        public DatePickerFragment(boolean start) {
            setStartDate = start;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            ProviderListActivity activity = ((ProviderListActivity)getActivity());

            month++; //dunno why I have to do this

            try {
                if (setStartDate)
                    activity.startDate = textFormat.parse(year + "-" + month + "-" + day);
                else
                    activity.endDate = textFormat.parse(year + "-" + month + "-" + day);
            } catch (Exception ex) {}
            activity.update();
        }
    }
}
