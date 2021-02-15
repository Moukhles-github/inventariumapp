package gb.j2.inventarium;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddRequest extends AppCompatDialogFragment
{
    private TextView WorkstationIdTextView;
    private Spinner ItemsTypesSpinnerView;
    private Spinner ItemsSpinnerView;
    private Spinner EmployeesSpinnerView;

    ArrayList<String> itemAList;
    ArrayList<Integer> itemAVal;

    ArrayList<String> itemTypesAList;
    ArrayList<Integer> itemTypesAVal;

    ArrayList<String> employeesAList;
    ArrayList<Integer> employeesAVal;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_request, null);

        builder.setView(view)
                .setTitle("Add New Request")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        int itemID =  itemAVal.get(ItemsSpinnerView.getSelectedItemPosition());
                        int employeeID = employeesAVal.get(EmployeesSpinnerView.getSelectedItemPosition());
                        String requestUrl4 = utility.baseUrl + "ws/ws_request.php?op=28&user_id=" + session.ID() + "&rqst_item=" + itemID + "&wrkst_id=" + session.WorkstationID() + "&ret=1&rqst_emp=" +employeeID;
                        wsInterface apiCall4 = new wsInterface(requestUrl4, 4);

                        apiCall4.execute();
                    }
                });

        WorkstationIdTextView = (TextView) view.findViewById(R.id.textViewAddRequestWorkstationID);
        ItemsSpinnerView = (Spinner) view.findViewById(R.id.itemsSpinner);
        EmployeesSpinnerView = (Spinner) view.findViewById(R.id.employeesSpinner);
        ItemsTypesSpinnerView = (Spinner) view.findViewById(R.id.itemsTypesSpinner);

        WorkstationIdTextView.setText("Workstation: " + session.WorkstationName() );

        getEmployees();
        getItemsTypes();
        return builder.create();
    }

    private void exitDialogue()
    {

    }

    private void getItemsTypes()
    {
        String requestUrl3 = utility.baseUrl + "ws/ws_item_type.php?op=1";
        wsInterface apiCall3 = new wsInterface(requestUrl3, 3);

        apiCall3.execute();
    }

    private void getItems(int id)
    {
        String requestUrl1 = utility.baseUrl + "ws/ws_item.php?op=15&typeID=" + String.valueOf(id);
        wsInterface apiCall1 = new wsInterface(requestUrl1, 1);

        apiCall1.execute();
    }

    private void getEmployees()
    {
        String requestUrl2 = utility.baseUrl + "ws/ws_employees.php?op=11&wrksid=" + session.WorkstationID();
        wsInterface apiCall2 = new wsInterface(requestUrl2, 2);
        apiCall2.execute();
    }

    //load categories for spinner
    private void FillItemTypesSpinner(String raw)
    {
        itemTypesAList = new ArrayList<String>();
        itemTypesAVal = new ArrayList<Integer>();

        try
        {
            JSONArray jArray = new JSONArray(raw);
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jObj = jArray.getJSONObject(i);

                itemTypesAList.add(jObj.getString("item_type_name"));
                itemTypesAVal.add(jObj.getInt("item_type_id"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        fillItemsTypes();
    }

    //load categories for spinner
    private void FillItemSpinner(String raw)
    {
        itemAList = new ArrayList<String>();
        itemAVal = new ArrayList<Integer>();

        try
        {
            JSONArray jArray = new JSONArray(raw);
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jObj = jArray.getJSONObject(i);

                itemAList.add(jObj.getString("item_name"));
                itemAVal.add(jObj.getInt("item_id"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        fillItems();
    }

    //load categories for spinner
    private void FillEmployeesSpinner(String raw)
    {
        employeesAList = new ArrayList<String>();
        employeesAVal = new ArrayList<Integer>();

        try
        {
            JSONArray jArray = new JSONArray(raw);
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jObj = jArray.getJSONObject(i);

                employeesAList.add(jObj.getString("emp_name"));
                employeesAVal.add(jObj.getInt("emp_id"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        fillEmployees();
    }

    //fill categories spinner
    private void fillItemsTypes()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemTypesAList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ItemsTypesSpinnerView.setAdapter(adapter);
        //ItemsTypesSpinnerView.setSelection(0, false);
        addSpinnersListeners();
    }

    //fill categories spinner
    private void fillItems()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemAList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ItemsSpinnerView.setAdapter(adapter);
        ItemsSpinnerView.setSelection(0, false);
    }

    //fill categories spinner
    private void fillEmployees()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, employeesAList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        EmployeesSpinnerView.setAdapter(adapter);
        EmployeesSpinnerView.setSelection(0, false);
    }


    //add select listener for order spinner
    private void addSpinnersListeners()
    {
        ItemsTypesSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                getItems(itemTypesAVal.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }

        });
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
                Log.e("Error", "Connection Error");
            }
            else
            {
                switch (operation)
                {
                    //load recommended data
                    case 1:
                    {
                        if (!rawData.equals(""))
                        {
                            FillItemSpinner(rawData);
                        }
                    }
                    break;
                    //fill category spinner
                    case 2:
                    {
                        if (!rawData.equals(""))
                        {
                            FillEmployeesSpinner(rawData);
                        }
                    }
                    break;
                    //get searched data
                    case 3:
                    {
                        if (!rawData.equals(""))
                        {
                            FillItemTypesSpinner(rawData);
                        }
                    }
                    break;
                    case 4:
                    {
                        if (!rawData.equals(""))
                        {
                            exitDialogue();
                        }
                    }
                    break;
                    default:
                        Log.e("Error", "Invalid Operation");
                        break;
                }
            }
        }
    }


//    case 16:
//    {
//        $result = $request->expresrqst($_GET["user_id"], $_GET["rqst_item"], $_GET["wrkst_id"], $_GET["ret"], $_GET["rqst_emp"]);
//    }
//				break;
}
