package com.gil_becker.nutritionreport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gil-B on 07/03/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String LOGCAT_TAG = "gil: ";

    private static final int DATABASE_VERSION = 1;

    protected static final String DATABASE_NAME = "nutritionDB";

    // Tables Names
    final String TABLE_FOODS = "foods";
    protected static final String TABLE_CONSUMED_FOODS = "consumedfoods";

    // FOODS Table - column names
    private static final String COLUMN_FOOD_ID = "_id"; //The column type is int, UNIQUE
    private static final String COLUMN_FOOD_NAME = "name"; //text, THIS IS THE PRIMARY KEY

    // CONSUMEDFOODS Table - column names
    private static final String COLUMN_CONSUMEDFOOD_ID = "_id";  //int, THIS IS THE PRIMARY KEY
    private static final String COLUMN_CONSUMEDFOOD_CREATED_AT = "createdate"; //type is int
    private static final String COLUMN_CONSUMEDFOOD_NAME = "name"; //text, FOREIGN KEY
    private static final String COLUMN_CONSUMEDFOOD_AMOUNT = "amount";// type is int

    private final String CREATE_TABLE_CONSUMED_FOODS =
            "CREATE TABLE " + TABLE_CONSUMED_FOODS + "("
                    + COLUMN_CONSUMEDFOOD_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_CONSUMEDFOOD_CREATED_AT + " INT, "
                    + COLUMN_CONSUMEDFOOD_NAME + " TEXT, "
                    + COLUMN_CONSUMEDFOOD_AMOUNT + " INT, "
                    + "CONSTRAINT consumedfoods FOREIGN KEY (" + COLUMN_CONSUMEDFOOD_NAME + ") " +
                    "REFERENCES " + TABLE_FOODS + " (name));";

    void prnt(Object object) {
        System.out.println("gil: " + object);
    }

    //=========================================================================

    protected boolean DatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        System.out.println("gil: context.getDatabasePath(dbName)="+context.getDatabasePath(dbName));
        return dbFile.exists();
    }

    //============================================================================

    protected boolean tableExist (SQLiteDatabase database , String table){
        boolean flag = false;
        try {
            Cursor tableCursor = database.rawQuery("SELECT * FROM " + table, null);
            if (tableCursor.moveToFirst()) flag = true;
        }
        catch (RuntimeException e){
            prnt(e);
        }
        return flag;
    }

    //============================================================================
    /**
     * create String for execute CREATE SQL TABLE
     * @param nutrients list (from random food report got from USDA web site)
     * @return string
     */

    public String createFoodTable(List<String> nutrients){

        StringBuilder createTableFood = new StringBuilder();

        String part1 = "CREATE TABLE IF NOT EXISTS "
                + TABLE_FOODS + " ("
                + COLUMN_FOOD_ID + " INTEGER UNIQUE, "
                + COLUMN_FOOD_NAME + " TEXT PRIMARY KEY, ";
        createTableFood.append(part1);
        for (String nutrientName :
                nutrients) {
            String nutrientsColumn = foramtNutrientNameForDb(nutrientName) + " FLOAT DEFAULT 0, ";
            createTableFood.append(nutrientsColumn);
        }

        String part3 = "UNIQUE (" + COLUMN_FOOD_ID + ") ON CONFLICT REPLACE );";
        createTableFood.append(part3);
        String tmp = createTableFood.toString();
        return tmp;
    }

    //=========================================================================

    /**
     * format the nutrient name for db
     * @param nutrientName (from json)
     * @return nutrient name (for db)
     */

    public String foramtNutrientNameForDb(String nutrientName){
        nutrientName = nutrientName.replaceAll("in ","in");
        nutrientName = nutrientName.replaceAll("[()]","");
        nutrientName  = nutrientName.replaceAll("[, -+-]"," ");
        nutrientName = nutrientName.replaceAll("  "," ");
        nutrientName = nutrientName.replaceAll(" ","_");
        return nutrientName;
    }
    //=========================================================================

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //=========================================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONSUMED_FOODS);
    }
    //=========================================================================

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println(LOGCAT_TAG +
                " Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMED_FOODS);
        onCreate(db);
    }
    //=========================================================================

//    public long addFoodItemRecord(SQLiteDatabase , ArrayList<String>foodItem ){
//
//        return ;
//    }

    //==========================================================================

    public void printColumnNames(SQLiteDatabase db , String table) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        String str[] = cursor.getColumnNames();
        StringBuilder columns = new StringBuilder();
        columns.append(table + ":");
        for (String tmp :
                str) {
            columns.append(tmp.toString() + ", ");
        }
        columns.deleteCharAt(columns.length()-2);
        prnt(columns.toString());
    }

    //=========================================================================

    public void deleteDatabase(SQLiteDatabase db) {
        db.execSQL("DROP DATABASE IF EXIST " + DATABASE_NAME + ";");
    }
    //=========================================================================

    public void deleteTable(SQLiteDatabase db, String table) {
        db.execSQL("DROP TABLE " + table + ";");
    }
    //=========================================================================

    public void printCursorToConsole(Cursor c) {
        List<String> cursorRow = new ArrayList<String>();
        for (int i = 0; i < c.getColumnNames().length; i++) {
            cursorRow.add(c.getString(c.getColumnIndex(c.getColumnName(i))));
        }
        prnt("Cursor=" + cursorRow);
    }

    //=========================================================================

   /**
     * Getting all food records from the Table
     */
    public List<FoodItem> getAllFoodItemsFromTheTable(SQLiteDatabase db , String table) {

        List<FoodItem> foodItemsList = new ArrayList<FoodItem>();
        String selectQuery = "SELECT  * FROM " + table;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                int id = c.getInt(c.getColumnIndex(COLUMN_FOOD_ID));
                String name = c.getString(c.getColumnIndex(COLUMN_FOOD_NAME));
                printCursorToConsole(c);
                c.moveToNext();
            }
        }
        c.close();
        return foodItemsList;
    }
    //============================================================================

    /**
     * print on monitor the user database
     * @param database
     */

    public void printTable (SQLiteDatabase database , String table) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + table , null);
        prnt("tabel name : "+table);
//        System.out.println("gil: cursor.getColumnNames()"+cursor.getColumnNames().length);
        ArrayList<String> columns = new ArrayList<>();
        for (String column:cursor.getColumnNames()) {
            columns.add(column);
        }
        prnt(columns);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (!columns.isEmpty()) columns.clear();
                for (int i = 0 ; i<cursor.getColumnNames().length ; i++){
                    String dbLineData = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
                    columns.add(dbLineData);
                }
                prnt(columns);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
    //============================================================================

    //=========================================================================
    /**
     * Getting all foods records NAMES from the Table
     */
    public ArrayList<String> getAllFoodNamesFromTheTable(SQLiteDatabase db) {

        ArrayList<String> foodsNamesList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOODS;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String food = c.getString(c.getColumnIndex(COLUMN_FOOD_NAME));
                foodsNamesList.add(food);
//                printCursorToConsole(c);
                c.moveToNext();
            }
        }
        c.close();
        return foodsNamesList;
    }

    //=========================================================================

    /**
     * Adding a new consumed food record to the Table
     */
    public long addConsumedItemRecord(SQLiteDatabase db, String name,
                                      int amount, String createdAt) {
        System.out.println(LOGCAT_TAG + "inserting consumed: " + name);
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_CONSUMEDFOOD_AMOUNT, amount);
        values.put(COLUMN_CONSUMEDFOOD_CREATED_AT, createdAt);
        long newRowId = db.insert(TABLE_CONSUMED_FOODS, null, values);
        return newRowId;
    }

    //=========================================================================

    /**
     * Getting all CONSUMED food records from the Table
     */
    public List<ConsumedFood> getAllConsumedFromTable(SQLiteDatabase db) {

        List<ConsumedFood> consumedFoodItemsList = new ArrayList<ConsumedFood>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONSUMED_FOODS;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(COLUMN_CONSUMEDFOOD_NAME));
                int id = c.getInt(c.getColumnIndex(COLUMN_CONSUMEDFOOD_ID));
                int date = c.getInt(c.getColumnIndex(COLUMN_CONSUMEDFOOD_CREATED_AT));
                int amount = c.getInt(c.getColumnIndex(COLUMN_CONSUMEDFOOD_AMOUNT));

                ConsumedFood food = new ConsumedFood(id, name, date , amount);
                consumedFoodItemsList.add(food);
                printCursorToConsole(c);
                c.moveToNext();
            }
        }
        c.close();
        return consumedFoodItemsList;
    }
    //=========================================================================
    // closing database

    public void closeDB() {
        if (db != null && db.isOpen())
            db.close();
    }

    //=========================================================================
    /**
     * get datetime
     * for easy insertion of date, for example:
     *     values.put(COLUMN_CONSUMEDFOOD_CREATED_AT, currDate());
     */
     public String currDate() {
         SimpleDateFormat dateFormat = new SimpleDateFormat(
         "dd-MM-yyyy", Locale.getDefault());
         Date date = new Date();
         return dateFormat.format(date);
     }
    //=========================================================================

    public HashMap manufactureNutriritionHashMap (SQLiteDatabase db) {
        String findFood = "SELECT * FROM " +
                TABLE_FOODS + " ; ";
        Cursor foodNutritionCursor = db.rawQuery(findFood, null);
        if (foodNutritionCursor.moveToFirst()) foodNutritionCursor.moveToFirst();
        printCursorToConsole(foodNutritionCursor);
        prnt("----------------------------------");
        String[] nutritionName = foodNutritionCursor.getColumnNames();
        HashMap<String , Float> nutritionMap = new HashMap();
        for (int i = 0; i < nutritionName.length; i++) {
            if (nutritionName[i].equals(COLUMN_FOOD_ID) || nutritionName[i].equals(COLUMN_FOOD_NAME)) {
            } else {
                nutritionMap.put(nutritionName[i], 0.0f);
            }
        }
        foodNutritionCursor.close();
        prnt("nutritionMap = " + nutritionMap);
        return nutritionMap;
    }

    //=========================================================================


    /**
     * find the food name by date in table "consumed food"
     * @param db
     * @param date
     */
    public HashMap getConsumedFoodByDate (SQLiteDatabase db , String date , HashMap nutritionMap){

        // selectQuery = the food name by date
        String selectQuery = "SELECT * FROM " + TABLE_CONSUMED_FOODS +
                " WHERE " + COLUMN_CONSUMEDFOOD_CREATED_AT + " = '" + date + "'";
        Cursor foodNameByDateCursor = db.rawQuery(selectQuery, null);
        if (foodNameByDateCursor.moveToFirst()) {

            while (!foodNameByDateCursor.isAfterLast()) {
                printCursorToConsole(foodNameByDateCursor);
                nutritionMap = getNutritionAmountPerFood(db  , foodNameByDateCursor , nutritionMap);
                foodNameByDateCursor.moveToNext();
            }
        }
        foodNameByDateCursor.close();
        return nutritionMap;
    }
    //=========================================================================

    /**
     * find the food line by food name string in table "foods"
     */

    public HashMap getNutritionAmountPerFood (SQLiteDatabase db, Cursor foodNameByDateCursor, HashMap nutritionMap){
        {
            //get the food name to string
            String foodName = foodNameByDateCursor.getString(foodNameByDateCursor.getColumnIndex(COLUMN_CONSUMEDFOOD_NAME));

            //get the amount of the food consumed per day from table consumed food
            Integer amount = foodNameByDateCursor.getInt(foodNameByDateCursor.getColumnIndex(COLUMN_CONSUMEDFOOD_AMOUNT));

            String findFood = "SELECT * FROM " +
                    TABLE_FOODS + " WHERE " +
                    COLUMN_FOOD_NAME + " = '" + foodName +"';";

            Cursor foodNutritionCursor = db.rawQuery(findFood, null);
            if (foodNutritionCursor.moveToFirst()) foodNutritionCursor.moveToFirst();
            {

                //*********************************************************************************************
                // go over the food raw and get the nutrition amount
                int columnCounter = foodNutritionCursor.getColumnCount();
                for (int i  = 0 ; i < columnCounter ; i++){
                    String nutrition = foodNutritionCursor.getColumnName(i);//column name = nutrition name
                    if (nutrition.equals(COLUMN_FOOD_ID) || nutrition.equals(COLUMN_FOOD_NAME))
                    {}
                    else {
                        //get the nutrition out of the food raw from "foods" table
                        String nutritionAmountInfood = foodNutritionCursor.getString(foodNutritionCursor.getColumnIndex(nutrition));
                        Float temp = Float.parseFloat(nutritionAmountInfood)*amount/100;
                        //update the nutrition sum
                        Float f = (Float)nutritionMap.get(nutrition)+temp;
                        nutritionMap.put(nutrition,f);

                    }
                }
                //**************************************************************************************************
            }
            foodNutritionCursor.close();
        }
        return nutritionMap;
    }
    //=========================================================================
    public List datesForReport (SQLiteDatabase db){
        List<String> dates = new ArrayList<>();
        String findDates = "SELECT " + COLUMN_CONSUMEDFOOD_CREATED_AT + " FROM " +
                TABLE_CONSUMED_FOODS + " ;";
        Cursor datesCursor = db.rawQuery(findDates, null);
        if (datesCursor.moveToFirst()) {
            while (!datesCursor.isAfterLast()) {
                printCursorToConsole(datesCursor);
                String date = datesCursor.getString(datesCursor.getColumnIndex(COLUMN_CONSUMEDFOOD_CREATED_AT));
                if (!dates.contains(date))dates.add(date);
                datesCursor.moveToNext();
            }
        }
        prnt(dates);
        return dates;
    }

}

