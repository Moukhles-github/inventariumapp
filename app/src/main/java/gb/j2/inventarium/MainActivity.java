package gb.j2.inventarium;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    //elements for hiding
    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox remeberMe;
    private Button loginBtn;

    private TextView signupLink;

    //prefs
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEdit;

    private ImageView loadingLogo;
    private AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingLogo = (ImageView) findViewById(R.id.imageViewLoading);
        loadingAnimation = (AnimationDrawable) loadingLogo.getDrawable();
        loadingLogo.setVisibility(View.INVISIBLE);


        //get values from editTexts
        usernameInput = (EditText) findViewById(R.id.editTextUsername);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        remeberMe = (CheckBox) findViewById(R.id.checkBox);
        loginBtn = (Button) findViewById(R.id.button_login);

        //get shared prefrences data
        prefs = getSharedPreferences("inventarium", MODE_PRIVATE);
        //clear shared prefs
        prefsEdit = getSharedPreferences("inventarium", MODE_PRIVATE).edit();

        checkAutoFill();
    }

    private void checkAutoFill()
    {
        //auto fill
        if(prefs.getBoolean("remember", false))
        {
            usernameInput.setText(prefs.getString("remUname", ""));
            passwordInput.setText(prefs.getString("remPass", ""));
            remeberMe.setChecked(true);
        }
    }

    public void loginAction(View v)
    {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        //instantiate the api web interface
        String requestUrl = utility.baseUrl+"ws/ws_users.php?op=15&uname="+username+"&upwd="+password;
        wsInterface apiCall = new wsInterface(requestUrl, 1);

        apiCall.execute();
    }

    private void animate(int tgl)
    {
        //when the call finishes
        if(tgl == 0)
        {
            loadingAnimation.stop();
            loadingLogo.setVisibility(View.INVISIBLE);
            loginBtn.setEnabled(true);
        }
        //when the call start
        else
        {
            loadingAnimation.start();
            loadingLogo.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
        }
    }

    private void onLogin(String raw)
    {

        if(raw.equals("0"))
        {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
            //show wrong user or password toast and reset fields
            usernameInput.setText("");
            passwordInput.setText("");
        }
        else
        {
            try
            {
                JSONObject jObject = new JSONObject(raw);
                session.SetID(jObject.getInt("user_id"));
                session.SetName(jObject.getString("user_name"));
                session.SetType(jObject.getString("user_type"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            if (remeberMe.isChecked())
            {
                prefsEdit.putString("remUname", usernameInput.getText().toString());
                prefsEdit.putString("remPass", passwordInput.getText().toString());
                prefsEdit.putBoolean("remember", true);
                prefsEdit.commit();
            }
            else
            {
                prefsEdit.putBoolean("remember", false);
                prefsEdit.commit();
            }
            if(session.Type().equals("2"))
            {

                //instantiate the api web interface
                String requestUrl2 = utility.baseUrl + "ws/ws_users.php?op=16&oprid=" + session.ID();
                wsInterface apiCal2 = new wsInterface(requestUrl2, 2);

                apiCal2.execute();
            }
            else
            {
                Toast.makeText(this, "Only operators can sign in", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class wsInterface extends AsyncTask
    {
        private String rawData = "";
        int operation;
        String urlIn;

        public wsInterface(String urlInput, int op) {
            operation = op;
            urlIn = urlInput;
        }

        @Override
        protected void onPreExecute() {
            if (operation == 1)
            {
                animate(1);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                URL webUrl = new URL(urlIn);
                //http request
                HttpURLConnection httpURLConnection = (HttpURLConnection) webUrl.openConnection();
                //input stream
                InputStream inputStream = httpURLConnection.getInputStream();
                //buffer reader to read from the stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        rawData = rawData + line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("testing", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(operation == 1)
            {
                if(!rawData.equals(""))
                {
                    onLogin(rawData);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Could not sign in", Toast.LENGTH_SHORT).show();
                }
                animate(0);
            }
            else
            {
                if(!rawData.equals(""))
                {
                    try
                    {
                        JSONArray jArray = new JSONArray(rawData);
                        for (int i = 0; i < jArray.length(); i++)
                        {
                            JSONObject jObject = jArray.getJSONObject(i);

                            session.SetWorkstationID(jObject.getInt("workstationID"));
                            session.SetWorkstationName(jObject.getString("workstationName"));
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent i;
                    i = new Intent(MainActivity.this, MainSearchActivity.class);
                    i.putExtra("initial", 1);
                    i.putExtra("keyword", "");
                    startActivity(i);

                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}