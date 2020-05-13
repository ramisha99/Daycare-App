package edu.lawrence.daycare;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.lawrence.daycare.EditChildActivity.PARENT_ID;

public class ChildrenListActivity extends AppCompatActivity {
    int parentId;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        parentId = getIntent().getIntExtra(PARENT_ID, -1);
        setContentView(R.layout.activity_children_list);

        // the floating action button is used for the "add child" action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::addChild);

        // get the listview that lists the children
        ListView list = findViewById(R.id.childList);

        // add an OnItemClick listener, this snippet of code will be executed whenever the user
        // taps on an item
        list.setOnItemClickListener((p, v, pos, id) -> {
            Child child = (Child)p.getItemAtPosition(pos);
            Log.d("ChildrenListActivity", "child: " + child.childId);
            editChild(child.childId);
        });

        // create a RetrieveChildrenTask to ask the server for a list of the children who belong to
        // this parent and then display those children to the user
        new RetrieveChildrenTask(parentId).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // onResume() is called whenever the activity appears on the screen, including after an activity
        // created by this activity is terminated
        // the list of children might have changed when we're returning to this activity (maybe the
        // user has added a new child) so check that again
        new RetrieveChildrenTask(parentId).execute();
    }

    // convenience method for launching an edit child activity using a Child object
    private void editChild(Child child) { editChild(child.childId); }

    // launches an edit child activity for the given child ID
    private void editChild(int id) {
        Intent intent = new Intent(this, EditChildActivity.class);

        // inform the EditChildActivity of the parent ID and the child ID, also let it know that
        // we're editing an existing child
        intent.putExtra(EditChildActivity.ACTIVITY_MODE, "edit");
        intent.putExtra(PARENT_ID, parentId);
        intent.putExtra(EditChildActivity.CHILD_ID, id);
        startActivity(intent);
    }

    private void addChild(View view) {
        Intent intent = new Intent(this, EditChildActivity.class);

        // inform the EditChildActivity of the parent ID and let it know that we're creating a new child
        intent.putExtra(EditChildActivity.ACTIVITY_MODE, "create");
        intent.putExtra(PARENT_ID, parentId);
        startActivity(intent);
    }

    // does the following:
    //
    // ask the server for a list of children belonging to a particular parent
    // put that list of children (a Child[]) into the listview
    private class RetrieveChildrenTask extends AsyncTask<Void, Void, String> {
        private String uri;
        RetrieveChildrenTask(int id) {
            uri = "http://" + URIHandler.hostName + "/children?parent=" + id;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri, "");
        }

        @Override
        protected void onPostExecute(String result) {
            //                                      this part converts the response into a Child[]
            //                                      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            ChildAdapter adapter = new ChildAdapter(gson.fromJson(result, Child[].class));
            ListView list = findViewById(R.id.childList);
            list.setAdapter(adapter);
        }
    }

    public class ChildAdapter extends BaseAdapter {
        List<Child> children;

        public ChildAdapter(Child[] children) {
            this.children = Arrays.asList(children);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position).toString());
            return convertView;
        }

        @Override
        public int getCount() {
            return children.size();
        }

        @Override
        public Child getItem(int position) {
            return children.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).childId; // assuming all received IDs are unique
        }
    }

}
