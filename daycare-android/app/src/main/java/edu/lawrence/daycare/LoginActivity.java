package edu.lawrence.daycare;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public final static String USER_ID = "edu.lawrence.daycare.USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void newUser(View view) {
        EditText userText = (EditText) findViewById(R.id.user_name);
        String userName = userText.getText().toString();
        EditText passwordText = (EditText) findViewById(R.id.password);
        String password = passwordText.getText().toString();
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new NewUserTask(userName,password).execute();
        } else {
            userMessage(getResources().getString(R.string.message_no_network));
        }
    }

    private class NewUserTask extends AsyncTask<Void, Void, String> {
        private String uri;
        private String user, password;
        NewUserTask(String userName,String password) {
            uri="http://"+URIHandler.hostName+"/user";
            this.user = userName;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doPost(uri,"{\"name\":\""+user+"\",\"password\":\""+password+"\"}");
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if("-1".equals(result))
                userMessage(getResources().getString(R.string.message_login_failed));
            else
                goToMain(result);
        }
    }

    public void logIn(View view) {
        // Fetch the user name and password from the GUI
        EditText userText = (EditText) findViewById(R.id.user_name);
        String userName = userText.getText().toString();
        EditText passwordText = (EditText) findViewById(R.id.password);
        String password = passwordText.getText().toString();

        // Create and launch the AsyncTask to check the user name and password.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new LogInTask(userName,password).execute();
        } else {
            userMessage(getResources().getString(R.string.message_no_network));
        }

    }

    private class LogInTask extends AsyncTask<Void, Void, String> {
        private String uri;

        LogInTask(String userName,String password) {
            uri="http://"+URIHandler.hostName+"/user?name="+userName+"&password="+password;
        }

        @Override
        protected String doInBackground(Void... ignored) {
            return URIHandler.doGet(uri,"-1");
        }

        @Override
        protected void onPostExecute(String result) {
            if("-1".equals(result))
                userMessage(getResources().getString(R.string.message_login_failed));
            else
                goToMain(result);
        }
    }

    private void userMessage(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void goToMain(String id) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(USER_ID, id);
        startActivity(intent);
    }

}
