package edu.lawrence.daycare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import java.security.ProviderException;

import static edu.lawrence.daycare.EditChildActivity.PARENT_ID;

public class DashboardActivity extends AppCompatActivity {
    int userId;
    int parentId = -1;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        userId = Integer.parseInt(getIntent().getStringExtra(LoginActivity.USER_ID));
        setContentView(R.layout.activity_dashboard);
        retrieveParentId(); // when the dashboard is first loaded, retrieve the parent id from the user id
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveParentId(); // in case we failed to retrieve the parent ID before, try again after
                            // returning to the activity, maybe the user created a profile in the
                            // meanwhile
    }

    protected void retrieveParentId() {

        // if we already have a parentId, this conditional will be equivalent to false and the
        // RetrieveParentIdTask will never be created/executed.
        // otherwise, parentId will be -1 and this code will execute
        if (parentId == -1)
            new RetrieveParentIdTask().execute();
    }
    public void editProfile(View view) {
        // create EditParentActivity in ACTIVITY_MODE == "edit", pass user id to it
        Intent intent = new Intent(this, EditParentActivity.class);
        intent.putExtra(LoginActivity.USER_ID, userId);
        intent.putExtra(EditParentActivity.ACTIVITY_MODE, "edit");
        startActivity(intent);
    }

    public void createProfile(View view) {
        // create EditParentActivity in ACTIVITY_MODE == "create", pass user id to it
        Intent intent = new Intent(this, EditParentActivity.class);
        intent.putExtra(LoginActivity.USER_ID, userId);
        intent.putExtra(EditParentActivity.ACTIVITY_MODE, "create");
        startActivity(intent);
    }

    public void viewChildren(View view) {
        // create ChildrenListActivity, pass user id and parent id to it
        Intent intent = new Intent(this, ChildrenListActivity.class);
        intent.putExtra(LoginActivity.USER_ID, userId);
        intent.putExtra(PARENT_ID, parentId);
        startActivity(intent);
    }

    public void findProviders(View view) {
        // create ProviderListActivity, pass parent id to it
        Intent intent = new Intent(this, ProviderListActivity.class);
        intent.putExtra(PARENT_ID, parentId);
        startActivity(intent);
    }

    // retrieves the parent ID of the parent belonging to a user ID
    // asks the server "what's the parent corresponding to this user ID" through /parent?user=<id>
    // parses the returned Parent object, puts its ID in the DashboardActivity's parentId variable
    // if there is no returned Parent object, the server couldn't find a Parent object for the given
    // user ID, then we set the parentId variable to -1 so we know to try again when we get the chance
    private class RetrieveParentIdTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveParentIdTask() {
            uri = "http://" + URIHandler.hostName + "/parent?user=" + userId;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("")) {
                parentId = -1;
                findViewById(R.id.editChildrenButton).setEnabled(false);
            }
            else {
                parentId = gson.fromJson(result, Parent.class).id;
                findViewById(R.id.editChildrenButton).setEnabled(true);
            }
        }
    }
}
