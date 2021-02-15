package gb.j2.inventarium;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RequestAdapter extends ArrayAdapter<request>
{
    private Context ActivityContext;
    private ArrayList<request> internalRequestList;
    private int intResource;
    private Context parentContext;

    private request currentRequest;

    public RequestAdapter(Context context, int resource, ArrayList<request> list) {
        super(context, 0 , list);
        ActivityContext = context;
        internalRequestList = list;
        intResource = resource;
        parentContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //create a layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(ActivityContext);

        View listItemView = layoutInflater.inflate(intResource, null, false);

        //get layout elements items
        TextView requestIDView = (TextView) listItemView.findViewById(R.id.textViewRequestID);
        TextView requestDateView = (TextView) listItemView.findViewById(R.id.textViewRequestDate);
        TextView requestItemTitleView = (TextView) listItemView.findViewById(R.id.textViewRequestItemName);
        TextView requestItemLabelView = (TextView) listItemView.findViewById(R.id.textViewRequestItemLabel);
        TextView requestStatusTextView = (TextView) listItemView.findViewById(R.id.textViewRequestStatusText);
        ImageView requestStatusImageView = (ImageView) listItemView.findViewById(R.id.imageViewRequestStatus);
        ImageButton requestStatusButton = (ImageButton) listItemView.findViewById(R.id.imageButtonRequestCancel);

        currentRequest = internalRequestList.get(position);

        //set tag for items
        listItemView.setTag(position);
        requestStatusButton.setTag(currentRequest.getID());

        requestIDView.setText("#"+currentRequest.getID());
        requestDateView.setText(currentRequest.getDate());
        requestItemTitleView.setText(currentRequest.getItemName());
        requestItemLabelView.setText(currentRequest.getItemLabel());
        requestStatusTextView.setText(currentRequest.GetStringStatus());

        request.setRatingImage(requestStatusImageView, currentRequest.getStatus());

        if(!currentRequest.IsCancelable())
        {
            requestStatusButton.setVisibility(View.GONE);
        }

        requestStatusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (parentContext instanceof MainSearchActivity) {
                    int id = (int) requestStatusButton.getTag();
                    int index = (int) listItemView.getTag();
                    String urlForeign = utility.baseUrl + "ws/ws_request.php?op=9&rqst_id="+id;

                    wsInterface apiCallForeign = new wsInterface(urlForeign, internalRequestList.get(index), requestStatusButton, requestStatusTextView, requestStatusImageView);
                    apiCallForeign.execute();
                }
            }
        });

        return listItemView;
    }
    //asynk task
    class wsInterface extends AsyncTask
    {
        private String rawData = "";
        String urlIn;
        ImageButton itemButton;
        TextView statusTextView;
        ImageView statusImageView;
        request currentRequest;

        private wsInterface(String urlInput, request req, ImageButton btn, TextView stattxt, ImageView img)
        {
            currentRequest = req;
            itemButton = btn;
            statusImageView = img;
            statusTextView = stattxt;
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
                Toast.makeText(parentContext, "Connection error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (rawData.equals("0"))
                {
                    currentRequest.SetStatus(-1);
                    statusTextView.setText(currentRequest.GetStringStatus());
                    request.setRatingImage(statusImageView, currentRequest.getStatus());
                    itemButton.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(parentContext, "Couldn't cancel request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}