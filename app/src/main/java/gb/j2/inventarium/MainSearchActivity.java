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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.ArrayList;

public class MainSearchActivity extends AppCompatActivity
{
    //this text view holds the no result found text view
    private TextView noResult;

    //fetched product list array
    private ArrayList<request> requestsList;
    private ListView listView;
    private RequestAdapter adapter;

    private Spinner spinner;
    private Spinner spinner2;

    //pagination variables
    private int currentPage = 1;
    private int pageCount;
    private boolean flag_loading = false;

    //previously selected preferences in search
    private int selectedCat;
    private int selectedOrder;
    private String searchedKeyword;

    //menu
    private Menu loadedMenu;

    //initial extra if = 1 then this is the home page and no pagination or search query needed
    private int initialExtra;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);

        //get list view
        listView = (ListView) findViewById(R.id.productListView);
        noResult = (TextView) findViewById(R.id.textViewNoResultFound);

        //action bar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        //get sent intent data
        Intent receivedIntent = getIntent();
        initialExtra = receivedIntent.getIntExtra("initial", -1);
        selectedCat = receivedIntent.getIntExtra("categoryID", 0);
        selectedOrder = receivedIntent.getIntExtra("orderID", 0);
        searchedKeyword = receivedIntent.getStringExtra("keyword");

        //load the order spinner from string array in resources
        spinner = (Spinner) findViewById(R.id.orderSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.orderSpinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //initialize category spinner
        spinner2 = (Spinner) findViewById(R.id.categorySpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> showSpinnerAdapter =  ArrayAdapter.createFromResource(this, R.array.showSpinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        showSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(showSpinnerAdapter);


        //initialise the product array
        requestsList = new ArrayList<>();


        //count pages for pagination
        getPagesCount(utility.baseUrl + "ws/ws_request.php?op=26&key=" + searchedKeyword + "&show=" + utility.showVal[selectedCat] + "&sort=" + (selectedOrder + 1) + "&oprid=" + session.ID());

        //set scroll listener for list view
        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                //check if the last item is in the screen if the bottom was reached
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0)
                {
                    //check if the page to load is smaller than the total number of pages and that it was not loaded before
                    if ((!flag_loading) && (currentPage < pageCount))
                    {
                        //set the already loaded to true
                        flag_loading = true;
                        String urlLoadMore = utility.baseUrl + "ws/ws_request.php?op=27&key=" + searchedKeyword + "&show=" + utility.showVal[selectedCat] + "&sort=" + (selectedOrder + 1) + "&page=" + (++currentPage) + "&oprid=" + session.ID();
                        //call page counter
                        wsInterface apiCallLoadMore = new wsInterface(urlLoadMore, 4);
                        apiCallLoadMore.execute();
                    }
                }
            }
        });
    }

    //get pages count
    //url parameter
    private void getPagesCount(String urlString)
    {
        //call page counter
        wsInterface apiCall3 = new wsInterface(urlString, 3);
        apiCall3.execute();
    }

    //get data from the resulted query
    private void getSearchedData()
    {
        String url4 = utility.baseUrl + "ws/ws_request.php?op=27&key=" + searchedKeyword + "&show=" + utility.showVal[selectedCat] + "&sort=" + (selectedOrder + 1) + "&page=" + currentPage + "&oprid=" + session.ID();
        //call page counter
        wsInterface apiCall4 = new wsInterface(url4, 1);
        apiCall4.execute();
    }

    //                                       common for all
    //on menu logo press
    public void takeMeHome(View v)
    {
        Intent i = new Intent(MainSearchActivity.this, MainSearchActivity.class);
        i.putExtra("initial", 1);
        i.putExtra("keyword", "");
        startActivity(i);
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

        //reuse the last searched word
        if (!(initialExtra == 1))
        {
            sView.setIconified(false);
            sView.setQuery(searchedKeyword, false);
        }

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
        //on text submit
        int chosenCat = spinner2.getSelectedItemPosition();
        int chosenOrder = spinner.getSelectedItemPosition();
        Intent searchIntent = new Intent(MainSearchActivity.this, MainSearchActivity.class);
        searchIntent.putExtra("initial", 2);
        searchIntent.putExtra("categoryID", chosenCat);
        searchIntent.putExtra("orderID", chosenOrder);
        searchIntent.putExtra("keyword", keyword);
        startActivity(searchIntent);
    }

    //                                          common for all



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
                requestsList.add(new request(jObj.getInt("rqst_id"), jObj.getString("item_name"), jObj.getString("item_label"), jObj.getInt("rqst_status"), jObj.getString("rqst_date")));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.e("url", e.toString());
        }
    }

    //fill listview with data
    private void fillList(String raw)
    {
        jsonConvert(raw);
        addSpinnersListeners();
        if (!requestsList.isEmpty())
        {
            adapter = new RequestAdapter(this, R.layout.normalitem, requestsList);

            listView.setAdapter(adapter);
        }
    }

    //add select listener for order spinner
    private void addSpinnersListeners()
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                //Toast.makeText(MainSearchActivity.this, searchedKeyword, Toast.LENGTH_SHORT).show();
                searchQuery(searchedKeyword);
                finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }

        });

        //add select value listener for category spinner
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                //Toast.makeText(MainSearchActivity.this, searchedKeyword, Toast.LENGTH_SHORT).show();
                searchQuery(searchedKeyword);
                finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }

        });
    }

    //update list view by updating updating the product list and refresh the adapter
    private void updateList(String raw)
    {
        jsonConvert(raw);

        adapter.notifyDataSetChanged();

        flag_loading = false;
    }

    //asynk task
    private class wsInterface extends AsyncTask
    {
        private String rawData = "";
        int operation;
        String urlIn;

        private wsInterface(String urlInput, int op)
        {
            operation = op;
            urlIn = urlInput;
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
            if (rawData.equals(""))
            {
                Toast.makeText(MainSearchActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                switch (operation)
                {
                    //load recommended data
                    case 1:
                    {
                        fillList(rawData);
                        addSpinnersListeners();
                    }
                    break;
                    //get searched data
                    case 3:
                    {
                        //set spinner value as was set after the spinner if fully loaded
                        spinner.post(new Runnable()
                        {
                            public void run()
                            {
                                spinner.setSelection(selectedOrder, false);
                            }
                        });
                        spinner2.post(new Runnable()
                        {
                            public void run()
                            {
                                spinner2.setSelection(selectedCat, false);
                            }
                        });
                        if (!rawData.equals("0"))
                        {
                            pageCount = Integer.valueOf(rawData);
                            getSearchedData();
                        }
                        else
                        {
                            //display no result found view
                            noResult.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                    //load more data
                    case 4:
                    {
                        updateList(rawData);
                    }
                    break;
                    default:
                        Toast.makeText(MainSearchActivity.this, "Invalid operation", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
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
                Intent i = new Intent(MainSearchActivity.this, AccountActivity.class);
                startActivity(i);
            }
            break;
            case R.id.menuLogout:
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                builder.setMessage("Do you really want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent i = new Intent(MainSearchActivity.this, MainActivity.class);
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

    //first time resume
    private boolean firstTimeResumed = false;

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        if (firstTimeResumed)
        {
            super.onPostResume();
        }
        firstTimeResumed = true;
    }
}