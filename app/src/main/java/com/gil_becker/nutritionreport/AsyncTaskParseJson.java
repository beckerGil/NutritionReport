package com.gil_becker.nutritionreport;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gil-B on 03-May-17.
 */

public class AsyncTaskParseJson extends AsyncTask {

    Handler handler = null;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    SQLiteDatabase database = null;
    DBHelper openHelper = null;
    Context mContext;

    //api key got from USDA
    final String ApiKey = "LsZjWtlMqrNVqeogq5EM0mNblWpSpv1kzT2Vgiey";

    //string for food search
    String itemNumber = "";

    final String TAG = "gil: ";

    String jsonStringUrlFood = "";

    List<String> foodNumbers;

    int counter = 0;

    Boolean done;

    static Connection conn;



    public AsyncTaskParseJson(Context context , String itemNumber , List<String> foodNumbers) {
        this.mContext = context;
        this.itemNumber = itemNumber;
        this.foodNumbers = foodNumbers;
        openHelper = new DBHelper(context);
        database = openHelper.getWritableDatabase();

    }

    @Override
    protected void onPreExecute() {

        String dataSource = "";

        jsonStringUrlFood = "https://api.nal.usda.gov/ndb/reports/?ndbno="+itemNumber+"&type=b&format=json&api_key="+ApiKey;



    }


    @Override
    protected Object doInBackground(Object[] params) {

            if (openHelper.tableExist(database, openHelper.TABLE_FOODS) == false) {
                System.out.println(TAG + "getting json data in background");
                createFoodTable();
                addFoodsToDB(foodNumbers);
            }
        openHelper.printTable(database, openHelper.TABLE_FOODS);

        return null;
    }

    //==============================================================================================

    /**
     * get json from USDA site
     * get nutirents names from json
     * create FOODS table columns
     * */
    protected void createFoodTable(){
        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            JSONObject jsonFood = jParser.getJSONFromUrl(jsonStringUrlFood);
            System.out.println(TAG+"json food= "+jsonFood);

            JSONObject report = jsonFood.getJSONObject("report");

            JSONObject food = report.getJSONObject("food");

            String ndbno = food.getString("ndbno");

            String name = food.getString("name");

            JSONArray nutrients = food.getJSONArray("nutrients");

            List nutrientsName = new ArrayList();

            // loop through all nutrients of the food
            for (int i = 0; i < nutrients.length(); i++) {

                JSONObject c = nutrients.getJSONObject(i);

                String nutrient_id = c.getString("nutrient_id");
                String nutrientName = c.getString("name");
                String value = c.getString("value");
                String unit = c.getString("unit");

                ContentValues nutrientsValues = new ContentValues();
                nutrientsValues.put("nutrient_id" , nutrient_id);
                nutrientsValues.put("nutrientName" , nutrientName);
                nutrientsValues.put("value" , value);
                nutrientsValues.put("unit" , unit);
                nutrientsName.add(nutrientName);
            }
            openHelper.prnt(nutrientsName);

            database.execSQL(openHelper.createFoodTable(nutrientsName));

            openHelper.printColumnNames(database, openHelper.TABLE_FOODS);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //==============================================================================================

    protected void addFoodsToDB(List<String> foodsNumber){
        openHelper.prnt("addFoodsToDB");
        for (String foodndbno :
                foodsNumber) {
            long newRowId = database.insert(openHelper.TABLE_FOODS, null, getFoodContent(foodndbno));
            openHelper.prnt("newRowId = " + newRowId);
        }
    }
    //==============================================================================================
    protected ContentValues getFoodContent(String ndbno) {

        ContentValues foodContent = new ContentValues();

        jsonStringUrlFood = "https://api.nal.usda.gov/ndb/reports/?ndbno=" + ndbno + "&type=b&format=json&api_key=" + ApiKey;

        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            JSONObject jsonFood = jParser.getJSONFromUrl(jsonStringUrlFood);
            openHelper.prnt("json food= " + jsonFood);

            // get the array of nutrients
            JSONObject report = jsonFood.getJSONObject("report");

            JSONObject food = report.getJSONObject("food");

            String name = food.getString("name");

            JSONArray nutrients = food.getJSONArray("nutrients");

            foodContent.put("name" , name);

            // loop through all nutrients of the food
            for (int i = 0; i < nutrients.length(); i++) {

                JSONObject c = nutrients.getJSONObject(i);

//                String nutrient_id = c.getString("nutrient_id");

                String nutrientName = c.getString("name");
                
                String value = c.getString("value");

//                String unit = c.getString("unit");

                foodContent.put( openHelper.foramtNutrientNameForDb(nutrientName) , value);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        counter++;
        foodContent.put("_id",counter);
        return foodContent;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        String result = "";
        if (handler != null) {
            Message message = new Message();
            message.obj = result;
            handler.sendMessage(message);
        }    }

}
