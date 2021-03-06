package team.mbl404.hcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{
    private static final int    DATABASE_VERSION   = 1;
    private static final String DATABASE_NAME      = "CLINICS.db";
    private static final String DB_TABLE_NAME      = "CLINIC_TABLE";
    private static final String DB_COLUMN_ID       = "ID";
    private static final String DB_COLUMN_NAME     = "NAME";
    private static final String DB_COLUMN_ADDRESS  = "ADDRESS";
    private static final String DB_COLUMN_CITY     = "CITY";
    private static final String DB_COLUMN_STATE    = "STATE";
    private static final String DB_COLUMN_ZIP      = "ZIP_CODE";
    private static final String DB_COLUMN_PHONE    = "TELEPHONE";
    private static final String DB_COLUMN_EMAIL    = "EMAIL";
    private static final String DB_COLUMN_WEBSITE  = "WEBSITE";
    private static final String DB_COLUMN_LATITUDE = "LATITUDE";
    private static final String DB_COLUMN_LONGITUDE= "LONGITUDE";
    private static final String DB_COLUMN_FAVORITE = "FAVORITE";

    private static final String DB_PATH = "/data/data/team.mbl404.hcare/databases/"+DATABASE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //builds table
    public void onCreate(SQLiteDatabase db) {
        String s_createDB = "create table " +
                DB_TABLE_NAME      + " (" +
                DB_COLUMN_ID       + " integer primary key, " +
                DB_COLUMN_NAME     + " text, " +
                DB_COLUMN_ADDRESS  + " text, " +
                DB_COLUMN_CITY     + " text, " +
                DB_COLUMN_STATE    + " text, " +
                DB_COLUMN_ZIP      + " integer, " +
                DB_COLUMN_PHONE    + " text, " +
                DB_COLUMN_EMAIL    + " text, " +
                DB_COLUMN_WEBSITE  + " text, " +
                DB_COLUMN_LATITUDE + " double, " +
                DB_COLUMN_LONGITUDE+ " double, " +
                DB_COLUMN_FAVORITE + " integer) ";
        db.execSQL(s_createDB);
        addSampleEntries(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> getAllClinics() {
        ArrayList<String> array_list = new ArrayList<>();

        Cursor res = this.getReadableDatabase().rawQuery("select * from " + DB_TABLE_NAME + "", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(DB_COLUMN_NAME)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getFavorites() {
        ArrayList<String> array_list = new ArrayList<>();

        Cursor res = this.getReadableDatabase().rawQuery("select * from " + DB_TABLE_NAME +
                " WHERE "+DB_COLUMN_FAVORITE+" = 1", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(DB_COLUMN_NAME)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public static ArrayList<String> searchFor(String query){
        SQLiteDatabase db = DBHelper.openDB();
        ArrayList<String> results = new ArrayList<>();
        String base = "SELECT * FROM "+ DB_TABLE_NAME+" ";
        String[] queries = new String[] {
                "WHERE "+DB_COLUMN_NAME   +" LIKE '%"+query+"%'",
                "WHERE "+DB_COLUMN_ADDRESS+" LIKE '%"+query+"%'",
                "WHERE "+DB_COLUMN_CITY   +" LIKE '%"+query+"%'",
                "WHERE "+DB_COLUMN_ZIP    +" LIKE '%"+query+"%'",
                "WHERE "+DB_COLUMN_STATE  +" LIKE '%"+query+"%'"};

        /*
        This didn't feel like the most efficient way to use SQL search, I was really hoping
        to find something like an OR function I could use between each of these requests so it
        could all be combined into a single request. But I guess passing over each column this way
        isn't so bad.
         */
        for (String newQuery : queries) {
            //Cleanest cursor call yet! Automatically closes curs because of try's resource manager
            try (Cursor curs = db.rawQuery(base + newQuery, null)) {
                while (curs.moveToNext()) {
                    results.add(curs.getString(curs.getColumnIndex(DB_COLUMN_NAME)));
                }
            }
        }
        db.close();


        return results;
    }

    public ShowClinic.Clinic getClinic(String name) {
        Cursor res = this.getReadableDatabase().rawQuery("SELECT * from "+DB_TABLE_NAME
                +" WHERE "+DB_COLUMN_NAME+"='"+name+"'",null);
        res.moveToFirst();
        ShowClinic.Clinic result = new ShowClinic.Clinic(
                res.getInt   (res.getColumnIndex(DB_COLUMN_ID)),
                res.getString(res.getColumnIndex(DB_COLUMN_NAME)),
                res.getString(res.getColumnIndex(DB_COLUMN_ADDRESS)),
                res.getString(res.getColumnIndex(DB_COLUMN_CITY)),
                res.getString(res.getColumnIndex(DB_COLUMN_STATE)),
                res.getInt   (res.getColumnIndex(DB_COLUMN_ZIP)),
                res.getString(res.getColumnIndex(DB_COLUMN_PHONE)),
                res.getString(res.getColumnIndex(DB_COLUMN_EMAIL)),
                res.getString(res.getColumnIndex(DB_COLUMN_WEBSITE)),
                res.getDouble(res.getColumnIndex(DB_COLUMN_LATITUDE)),
                res.getDouble(res.getColumnIndex(DB_COLUMN_LONGITUDE)),
                res.getInt   (res.getColumnIndex(DB_COLUMN_FAVORITE)));
        res.close();
        return result;
    }

    //Reverses the favorite of the entry
    public static void toggleFavorite(int id, int fav){
        SQLiteDatabase db = DBHelper.openDB();
        Cursor res = db.rawQuery("UPDATE "+DB_TABLE_NAME+" SET "+DB_COLUMN_FAVORITE+
                          " = "+fav+" WHERE "+DB_COLUMN_ID+" = "+id,null);
        res.moveToFirst();
        res.close();
        db.close();
    }

    //returns open handle
    public static SQLiteDatabase openDB(){
        return SQLiteDatabase.openDatabase(DB_PATH,null,SQLiteDatabase.OPEN_READWRITE);
    }
    //Adds a new entry
    private boolean addEntry(int id, String name, String addr, String city, String state, int zip,
                            String phone, String email, String web, Double lat, Double lon,
                             SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DB_COLUMN_ID, id);
        contentValues.put(DB_COLUMN_NAME, name);
        contentValues.put(DB_COLUMN_ADDRESS, addr);
        contentValues.put(DB_COLUMN_CITY, city);
        contentValues.put(DB_COLUMN_STATE, state);
        contentValues.put(DB_COLUMN_ZIP, zip);
        contentValues.put(DB_COLUMN_PHONE, phone);
        contentValues.put(DB_COLUMN_EMAIL, email);
        contentValues.put(DB_COLUMN_WEBSITE, web);
        contentValues.put(DB_COLUMN_LATITUDE, lat);
        contentValues.put(DB_COLUMN_LONGITUDE, lon);
        contentValues.put(DB_COLUMN_FAVORITE, 0);

        db.insert(DB_TABLE_NAME, null, contentValues);
        return true;
    }

    //Default sample entries
    private void addSampleEntries(SQLiteDatabase db) {
        addEntry(1,"PA HealthCare Clinic","480 S California Ave Ste 103","Palo Alto","CA",94306,
                "605-888-8888","paloaltoclinic@gmail.com","http://www.pamf.org/",
                37.4257,-122.1457,db);
        addEntry(2,"OC HealthCare Clinic","1508 Division St # 205","Oregon City","OR",97045,
                "503-888-8888","oregoncityclinic@gmail.com","http://ochealthinfo.com/phs/about/dcepi/ttc",
                45.3401,-118.0976,db);
        addEntry(3,"CS HealthCare Clinic","3230 E Woodmen Rd Ste 100","Colorado Springs","CO",80920,
                "719-888-8888","coloradospringsclinic@gmail.com","http://www.cshealthcare.co.uk/",
                38.9364,-104.7653,db);
        addEntry(4,"IF HealthCare Clinic","390 W 13th St","Idaho Falls","ID",83402,
                "208-888-8888","idahofallsclinic@gmail.com","http://www.familyhealthcareclinic.com/",
                43.4864,-112.0394,db);
        addEntry(5,"ST HealthCare Clinic","5290 McNutt Rd","Santa Teresa","TX",88008,
                "915-888-8888","santateresaclinic@gmail.com","http://www.familyhealthcareclinic.com/",
                31.8483,-106.6248,db);
        addEntry(6,"MC HealthCare Clinic","16222 Bothell","Mill Creek","WA",98012,
                "425-888-8888","millcreekclinic@gmail.com","http://www.pamf.org/",
                47.8517,-122.2201,db);
        addEntry(7,"SL HealthCare Clinic","2200 E Show Low Lake Rd","Show Low","AZ",85901,
                "928-888-8888","showlowclinic@gmail.com","https://intermountainhealthcare.org/locations/salt-lake-clinic/",
                34.2019,-110.0191,db);
        addEntry(8,"FM HealthCare Clinic","16251 N Cleveland Ave, Ste 7","Fort Meyers","FL",33903,
                "239-888-8888","fortmeyersclinic@gmail.com","http://www.healthcare311.com/clinic.php?id=1271",
                26.6983,-81.9014,db);
        addEntry(9,"IC HealthCare Clinic","1552 Mall Dr","Iowa City","IA",52240,
                "319-888-8888","iowacityclinic@gmail.com","http://freemedicalclinic.org/",
                41.6435,-91.5055,db);
        addEntry(10,"LV HealthCare Clinic","600 Mills Ave","Las Vegas","NM",87701,
                "505-888-8888","lasvegasclinic@gmail.com","http://www.manta.com/c/mt94nxx/lv-healthcare-inc",
                35.6063,-105.2222,db);
    }
}