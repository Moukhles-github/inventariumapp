package gb.j2.inventarium;

public class session
{
    private static String _name;
    private static int _id;
    private static String _type;
    private static String _workstationName;
    private static int _workstationID;

    //set
    public static void SetName(String name)
    {
        _name = name;
    }

    public static void SetWorkstationName(String workstationName)
    {
        _workstationName = workstationName;
    }

    public static void SetID(int id)
    {
        _id = id;
    }

    public static void SetWorkstationID(int id)
    {
        _workstationID = id;
    }

    public static void SetType(String typ)
    {
        _type = typ;
    }

    //get
    public static int ID()
    {
        return _id;
    }

    public static int WorkstationID()
    {
        return _workstationID;
    }

    public static String Type()
    {
        return _type;
    }

    public static String Uname()
    {
        return _name;
    }

    public static String WorkstationName()
    {
        return _workstationName;
    }
}
