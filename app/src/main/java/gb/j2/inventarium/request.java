package gb.j2.inventarium;

import android.widget.ImageView;

public class request
{
    //variables
    private int _rid;
    private String _itemName;
    private String _itemLabel;
    private int _status;
    private String _date;

    //constructor
    public request()
    {

    }
    public request(int id, String name, String label, int status, String date)
    {
        _rid = id;
        _itemName = name;
        _itemLabel = label;
        _status = status;
        _date = date;
    }


    //getters
    public int getID()
    {
        return _rid;
    }

    public String getItemName()
    {
        return _itemName;
    }

    public String getItemLabel()
    {
        return _itemLabel;
    }

    public int getStatus()
    {
        return _status;
    }

    public String getDate()
    {
        return _date;
    }

    //setters

    public void SetID(int id)
    {
        this._rid = id;
    }

    public void SetName(String name)
    {
        this._itemName = name;
    }

    public void SetLabel(String label)
    {
        this._itemLabel = label;
    }

    public void SetStatus(int status)
    {
        this._status = status;
    }

    public void SetDate(String date)
    {
        this._date = date;
    }


    //utilities
    public boolean IsCancelable()
    {
        if (_status == 0 || _status == 1)
        {
            return true;
        }

        return false;
    }

    public String GetStringStatus()
    {
        String Status;
        switch (_status)
        {
            case -1:
            {
                Status = "Canceled";
            }
            break;
            case 0:
            {
                Status = "Waiting";
            }
            break;
            case 1:
            {
                Status = "Accepted";
            }
            break;
            case 2:
            {
                Status = "Handled";
            }
            break;
            case 3:
            {
                Status = "Returned";
            }
            break;
            default:
            {
                Status = "Error";
            }
            break;
        }

        return Status;
    }


    public static void setRatingImage(ImageView imgView, int Status)
    {
        switch (Status)
        {
            case -1:
            {
                imgView.setImageResource(R.drawable.statcanceled);
            }
            break;
            case 0:
            {
                imgView.setImageResource(R.drawable.statwaiting);
            }
            break;
            case 1:
            {
                imgView.setImageResource(R.drawable.stataccepted);
            }
            break;
            case 2:
            {
                imgView.setImageResource(R.drawable.stathandeled);
            }
            break;
            case 3:
            {
                imgView.setImageResource(R.drawable.statreturn);
            }
            break;
            default:
            {
                imgView.setImageResource(R.drawable.staterror);
            }
            break;
        }
    }

}
