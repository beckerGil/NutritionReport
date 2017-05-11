package com.gil_becker.nutritionreport;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class DisplayReportActivity extends AppCompatActivity {

    SQLiteDatabase database = null;
    DBHelper openHelper = null;

    String date ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_report);

        openHelper = new DBHelper(this);
        database = openHelper.getWritableDatabase();

        openHelper.prnt("getAllConsumedFromTable = "+openHelper.getAllConsumedFromTable(database));

        //get today date
        String todayString = openHelper.currDate();
        //set date display
        TextView shownDate = (TextView) findViewById(R.id.shown_date);
        shownDate.setText(todayString);
        openHelper.prnt("date: "+todayString);
        setViewForList(todayString);
    }

    //============================================================================
    public void hideKeyboard()
    {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
    //===========================================================================

    /**
     * set action for choose date button
     * @param button
     */
    public void chooseDateButton(View button){
        final List dates = openHelper.datesForReport(database);
        ListView lv = (ListView) findViewById(R.id.consumed_nutritions_list);
        lv.setAdapter(new AdapterForTheListOnMainActivity(this,dates));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                date = dates.get(position).toString();
                setViewForList(date);
                TextView shownDate = (TextView) findViewById(R.id.shown_date);
                shownDate.setText(date);
            }
        });

    }

    /**
     * set the view for the list at the button
     * consumed dates / consumed nutrition by date
     * @param date
     */
    public void setViewForList (String date){
        ListView lv = (ListView) findViewById(R.id.consumed_nutritions_list);
        lv.setAdapter(new AdapterForTheListOnReportActivity(this,
                openHelper.getConsumedFoodByDate(database,date,openHelper.manufactureNutriritionHashMap(database))));

    }
}
