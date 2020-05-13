package edu.lawrence.daycare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class EditChildActivity extends AppCompatActivity {
    public static final String ACTIVITY_MODE = "edu.lawrence.daycare.ACTIVITY_MODE";
    public static final String CHILD_ID = "edu.lawrence.daycare.CHILD_ID";
    public static final String PARENT_ID = "edu.lawrence.daycare.PARENT_ID";

    Gson gson;
    Boolean editing = false;
    int parentId;
    int childId;

    SimpleDateFormat textFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        // get ACTIVITY_MODE, child ID, parent ID passed to us
        parentId = getIntent().getIntExtra(PARENT_ID, -1);
        childId = getIntent().getIntExtra(CHILD_ID, -1);
        editing = getIntent().getStringExtra(ACTIVITY_MODE).equals("edit");
        if (editing) {
            loadChild(); // if we're editing an existing child, retrieve its data and fill out the EditTexts
            setTitle("Edit child");
        }
        else
            setTitle("Create child");

        setContentView(R.layout.activity_edit_child);
    }

    // requests the child data from the server, fills out the EditTexts through RetrieveChildTask
    private void loadChild() {
        new RetrieveChildTask(childId).execute();
    }

    // this method is only used from within RetrieveChildTask, it takes a Child object and fills out
    // the EditTexts
    private void loadFromChild(Child c) {
        if (c == null)
            return;

        setEditTextContents(R.id.nameField, c.name);

        if (c.birthDate != null)
            setEditTextContents(R.id.birthdayField, textFormat.format(c.birthDate));
    }

    // creates a new Child object and fills it out with the data from the EditTexts
    private Child readToChild() {
        Child child = new Child();

        child.name = getEditTextContents(R.id.nameField);
        try {
            child.birthDate = textFormat.parse(getEditTextContents(R.id.birthdayField));

            // I have to add the following lines to make it work correctly (this fixes the issue where
            // the birthday keeps sliding back by one day every time the user saves their child
            Calendar c = Calendar.getInstance();
            TimeZone current = TimeZone.getTimeZone("America/Chicago");

            child.birthDate = new Date(child.birthDate.getTime() + 24 * 60 * 60 * 1000);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Invalid date of birth.", Toast.LENGTH_LONG);
            return null;
        }
        child.childId = childId;
        child.parentId = parentId;

        return child;
    }

    // returns the contents of an EditText given the layout ID
    private String getEditTextContents(int id) {
        return ((EditText)findViewById(id)).getText().toString();
    }

    // sets the contents of an EditText given the layout ID and the new text
    private void setEditTextContents(int id, String contents) {
        ((EditText)findViewById(id)).setText(contents);
    }

    // handler for the OK button
    public void okButton(View view) {
        Child child = readToChild(); // put the returned value of readToChild() in the child variable

        // readToChild() can return null, so we have to check for that
        if (child != null)
            new SubmitChildTask(child).execute(); // this executes if child contains a valid value
    }

    public void cancelButton(View view) {
        finish();
    }

    // retrieves a Child object from the server
    private class RetrieveChildTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveChildTask(int id) {
            uri = "http://" + URIHandler.hostName + "/child?id=" + id;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            //                 convert response to Child
            //            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            loadFromChild(gson.fromJson(result, Child.class));
        }
    }

    // submit a Child to the server
    private class SubmitChildTask extends AsyncTask<Void, Void, String> {
        private String uri;
        private String json;
        private boolean put;
        SubmitChildTask (Child child) {
            uri = "http://" + URIHandler.hostName + "/child";
            json = gson.toJson(child);
            put = editing; // if we're editing an existing child, we want to send a PUT request
            // otherwise we'd send a POST request
        }

        @Override
        protected String doInBackground(Void... ignored) {
            if (put) {
                return URIHandler.doPut(uri, json);
            }
            else {
                return URIHandler.doPost(uri, json);
            }
        }

        @Override
        // when we're done, close activity
        protected void onPostExecute(String result) {
            finish();
        }
    }
}
