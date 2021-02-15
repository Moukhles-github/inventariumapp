package gb.j2.inventarium;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
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

public class AccountActivity extends AppCompatActivity
{
    private TextView operatorInfoView;
    private String operatorInfo;

    //menu
    private Menu loadedMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //action bar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        operatorInfoView = (TextView) findViewById(R.id.textViewOperatorInfo);
        operatorInfo = "";
        getUserInfo();
    }

    private void getUserInfo()
    {
        String url4 = utility.baseUrl + "ws/ws_users.php?op=16&oprid=" + session.ID();
        //call page counter
        AccountActivity.wsInterface apiCall = new AccountActivity.wsInterface(url4, 1);
        apiCall.execute();
    }

    //convert json string to
    private void jsonConvert(String raw)
    {
        try
        {
            JSONArray jArray = new JSONArray(raw);
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jObj = jArray.getJSONObject(i);

                //add values to list
                operatorInfo += "User Name: " + jObj.getString("userName") + "\n";
                operatorInfo += "SSN: " + jObj.getString("SSN") + "\n";
                operatorInfo += "First Name: " + jObj.getString("firstName") + "\n";
                operatorInfo += "Last Name: " + jObj.getString("lastName") + "\n";
                operatorInfo += "Phone Number: " + jObj.getString("phoneNumber") + "\n";
                operatorInfo += "Address: " + jObj.getString("address") + "\n";
                operatorInfo += "Join Date: " + jObj.getString("joinDate") + "\n";
                operatorInfo += "Workstation Name: " + jObj.getString("workstationName") + "\n";
                operatorInfo += "Fouls: " + jObj.getString("fouls") + "\n";
                operatorInfo += "RFID: " + jObj.getString("RFID") + "\n";
                operatorInfo += "Company Name: " + jObj.getString("companyName") + "\n";
            }

            operatorInfoView.setText(operatorInfo);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.e("url", e.toString());
        }
    }


    //asynk task
    private class wsInterface extends AsyncTask
    {
        private String rawData = "";
        String urlIn;
        int operation;

        private wsInterface(String urlInput, int op)
        {
            urlIn = urlInput;
            operation = op;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            try
            {
                URL webUrl = new URL(urlIn);
                //http request
                HttpURLConnection httpURLConnection = (HttpURLConnection) webUrl.openConnection();
                //input stream
                InputStream inputStream = httpURLConnection.getInputStream();
                //buffer reader to read from the stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null)
                {
                    line = bufferedReader.readLine();
                    if (line != null)
                    {
                        rawData = rawData + line;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            //if there was no data loaded
            if (!rawData.equals(""))
            {
                jsonConvert(rawData);
            }
            else
            {
                Toast.makeText(AccountActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //new menu loader
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuuser, menu);

        loadedMenu = menu;

        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        SearchView sView = (SearchView) searchItem.getActionView();

        //on search keyword submitted in the menu
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                searchQuery(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });

        return true;
    }

    //search for items
    private void searchQuery(String keyword)
    {
        Intent searchIntent = new Intent(AccountActivity.this, MainSearchActivity.class);
        searchIntent.putExtra("initial", 2);
        searchIntent.putExtra("categoryID", 0);
        searchIntent.putExtra("orderID", 0);
        searchIntent.putExtra("keyword", keyword);

        startActivity(searchIntent);
    }

    //on menu logo press
    public void takeMeHome(View v)
    {
        Intent i = new Intent(AccountActivity.this, MainSearchActivity.class);
        i.putExtra("initial", 1);
        i.putExtra("keyword", "");
        startActivity(i);
    }

    //kill activity function to prevent errors
    private void killActivity()
    {
        this.finish();
    }

    //on menu item selected
    //common for all items
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.userMenuAcc:
            {
                Intent i = new Intent(AccountActivity.this, AccountActivity.class);
                startActivity(i);
            }
            break;
            case R.id.menuLogout:
            {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom);
                builder.setMessage("Do you really want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                killActivity();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Discard");
                d.show();
                d.setCancelable(false);
            }
            break;
            default:
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}