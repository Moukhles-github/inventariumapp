package gb.j2.inventarium;

public class session
{
    private static String _name;
    private static int _id;
    private static String _type;

    //set
    public static void SetName(String name)
    {
        _name = name;
    }

    public static void SetID(int id)
    {
        _id = id;
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

    public static String Type()
    {
        return _type;
    }

    public static String Uname()
    {
        return _name;
    }
}
