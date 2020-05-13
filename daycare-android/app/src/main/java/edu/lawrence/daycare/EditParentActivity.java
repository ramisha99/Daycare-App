package edu.lawrence.daycare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

public class EditParentActivity extends AppCompatActivity {
    public static final String ACTIVITY_MODE = "edu.lawrence.daycare.ACTIVITY_MODE";

    Gson gson;
    Boolean editing = false;
    Parent lastParent;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();

        userId = getIntent().getIntExtra(LoginActivity.USER_ID, -1);
        editing = getIntent().getStringExtra(ACTIVITY_MODE).equals("edit");
        if (editing) {
            loadProfile();
            setTitle("Edit parent profile");
        }
        else
            setTitle("Create parent profile");

        setContentView(R.layout.activity_edit_parent);
    }

    private void loadProfile() {
        new RetrieveParentTask(userId).execute();
    }

    private void loadFromParent(Parent p) {
        if (p == null)
            return;

        lastParent = p;

        setEditTextContents(R.id.nameField, p.name);
        setEditTextContents(R.id.addressField, p.address);
        setEditTextContents(R.id.cityField, p.city);
        setEditTextContents(R.id.emailField, p.email);
        setEditTextContents(R.id.phoneField, p.phone);
    }

    private Parent readToParent() {
        Parent parent = new Parent();

        parent.name = getEditTextContents(R.id.nameField);
        parent.address = getEditTextContents(R.id.addressField);
        parent.city = getEditTextContents(R.id.cityField);
        parent.email = getEditTextContents(R.id.emailField);
        parent.phone = getEditTextContents(R.id.phoneField);

        if (lastParent != null) {
            parent.id = lastParent.id;
        }

        parent.user = userId;

        return parent;
    }

    private String getEditTextContents(int id) {
        return ((EditText)findViewById(id)).getText().toString();
    }

    private void setEditTextContents(int id, String contents) {
        ((EditText)findViewById(id)).setText(contents);
    }

    public void okButton(View view) {
        new SubmitParentTask(readToParent()).execute();
    }

    public void cancelButton(View view) {
        finish();
    }

    private class RetrieveParentTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveParentTask(int id) {
            uri = "http://" + URIHandler.hostName + "/parent?user=" + id;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            loadFromParent(gson.fromJson(result, Parent.class));
        }
    }

    private class SubmitParentTask extends AsyncTask<Void, Void, String> {
        private String uri;
        private String json;
        private boolean put;
        SubmitParentTask (Parent parent) {
            uri = "http://" + URIHandler.hostName + "/parent";
            json = gson.toJson(parent);
            put = editing;
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
        protected void onPostExecute(String result) {
            finish();
        }
    }
}
