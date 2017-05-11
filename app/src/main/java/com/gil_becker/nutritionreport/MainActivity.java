package com.gil_becker.nutritionreport;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Context context;

    String fileName = "DBExist";

    SQLiteDatabase database = null;
    DBHelper openHelper = null;
    final static String dbName = "nutritionDB";
    int foodAmount;
    EditText et = null;
    String st = "";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String value = (String) msg.obj;
            populateListOnScreenFromTheDB(context);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        this.deleteDatabase(dbName);

        openHelper = new DBHelper(this);
        database = openHelper.getWritableDatabase();

        List<String> foodNumbers = new ArrayList<>();
        for (int i=1 ; i<10 ; i++){
            Random rnd = new Random();
            String number = "0100"+i;
            foodNumbers.add(number);
        }

        AsyncTaskParseJson taskParseJson = new AsyncTaskParseJson(this , "01009" , foodNumbers);

        taskParseJson.setHandler(handler);

        taskParseJson.execute();



//        openHelper.addFoodItemRecord(database, 1 , "Blue Cheese" , 353 , 28.75 , 21.4 , 2.34 , 0.0 , 198 , 2.66);
//        openHelper.addFoodItemRecord(database, 2 , "Milk chocolate" , 67 , 0 , 3.39 , 13.46 , 1.0 , 64 , 0.41);
//        openHelper.addFoodItemRecord(database, 3 , "Orange" , 47 , 0.12 , 0.94 , 11.75 , 53.2 , 11 , 0.07);
//        openHelper.addFoodItemRecord(database, 4 , "Egg" , 147 , 9.51 , 12.56 , 0.72 , 0.0 , 160 , 1.29 );
//        openHelper.addFoodItemRecord(database, 5 , "Grilled Breast Chicken" , 151 , 3.17 , 30.54 , 0.00 , 0.0, 10 , 0.90 );
//        openHelper.addFoodItemRecord(database, 7 , "Pita Bread" , 275 , 1.20 , 9.10	 , 55.70 , 0.0, 0.0 , 0.84 );
//        openHelper.addFoodItemRecord(database, 8 , "Almonds Nuts" , 579 , 49.93 , 21.15	 , 21.55 , 0.0, 0.0 , 3.12 );
//        openHelper.addFoodItemRecord(database, 9 , "Tomatoe" , 23 , 0.20 , 1.20	 , 5.10 , 23.4, 32 , 0.07 );
//        openHelper.addFoodItemRecord(database, 10 , "Cooked Pasta" , 131 , 1.05 , 5.15 , 24.93 , 0.0 , 6 , 0.56 );
//

        setButtonDisplayReport();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        }

    //============================================================================
        void prnt(Object object){
        System.out.println("gil: "+object);
        }
    //============================================================================

    public void populateListOnScreenFromTheDB(Context context)
    {

        final EditText et = (EditText) findViewById(R.id.amount_edit_text);
        et.setText("");
        ListView lv = (ListView) findViewById(R.id.foods_list);
        lv.setAdapter(new AdapterForTheListOnMainActivity(this,
                openHelper.getAllFoodNamesFromTheTable(database)));

        // hide the numeric keyborad on enter clicked
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    prnt("enter clicked");
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // the user added the consumed amount on the EditText
                //  let's get it
                st = et.getText().toString();
                // verify amount was entered
                if (st.isEmpty()) {
                    Toast.makeText(parent.getContext() , "TYPE AMOUNT IN GRAMS FIRST",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    foodAmount = Integer.parseInt(st);
                    prnt("foodAmount=" + foodAmount);

                    // The row on the this list which the user tapped on
                    //  is a TextView which displays a name of food, let's get it
                    String stFoodName = ((TextView) (view.findViewById(R.id.foodName))).getText().toString();
                    Toast.makeText(parent.getContext() ,foodAmount+" grams "+ stFoodName+" added",
                            Toast.LENGTH_LONG).show();

                    // the date of the new ConsumedFood object is today's date
                    String currentDate = openHelper.currDate();
                    // Add the new consumed food to the consumedfoods table
                    openHelper.addConsumedItemRecord(database, stFoodName, foodAmount, currentDate);
                    openHelper.printTable(database,DBHelper.TABLE_CONSUMED_FOODS);
                    // Clear the old user input from the EditText
                    et.setText("");
                }
            }
        });
    }
    //============================================================================
    public void hideKeyboard(View view)
    {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    //===========================================================================

    public void setButtonDisplayReport(){
        /**
         * action for display report
         */

        Button displayNutritionReport = (Button)findViewById(R.id.display_report_button);

        displayNutritionReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() , DisplayReportActivity.class);
                startActivity(intent);
            }
        });
    }

}