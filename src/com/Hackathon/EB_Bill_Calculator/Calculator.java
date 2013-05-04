package com.Hackathon.EB_Bill_Calculator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Calculator extends Activity implements ICallback{

    private Button VWsubmit;
    private EditText VWfromUnits;
    private EditText VWtoUnits;
    private Integer fromUnits;
    private Integer toUnits;
    private Button VWUpdate;
    private BillDetailsDataStore billDetailsDataStore;
    private ICallback activityClassObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        InitializeViewElements();
        billDetailsDataStore = new BillDetailsDataStore(getApplicationContext());
        activityClassObject=this;
        int rowsCount = billDetailsDataStore.getCount();
        if(rowsCount==0){
           updateBillRates(this.getApplicationContext());
        }
        VWsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                getInputUnits();
                validateInput(view);
                float cost = getBill(fromUnits, toUnits);
                toastShow(view, String.valueOf(cost));
                }
                catch (Exception e) {
                toastShow(view, "Invalid input");
                }
            }
        });

        VWUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBillRates(view.getContext());
            }
        });
    }

    private void updateBillRates(Context context) {
        String url = "http://guarded-badlands-3707.herokuapp.com/";
        new calculateAsyncTask(activityClassObject,context).execute(url);
    }

    private void InitializeViewElements() {
        VWsubmit = (Button) findViewById(R.id.submit);
        VWfromUnits = (EditText) findViewById(R.id.textFromUnit);
        VWtoUnits = (EditText) findViewById(R.id.textToUnits);
        VWUpdate=(Button) findViewById(R.id.update);

    }

    private void getInputUnits() {
        fromUnits = Integer.valueOf(VWfromUnits.getText().toString());
        toUnits = Integer.valueOf(VWtoUnits.getText().toString());
    }

    private float getBill(Integer fromUnits, Integer toUnits) throws JSONException {
        int totalUnits = toUnits - fromUnits;
        JSONArray slabRatesJson = getSlabRatesJson();
        return calculateCost(slabRatesJson, totalUnits);
    }

    private void validateInput(View view) {
        if(fromUnits<0 || toUnits<0 || fromUnits>toUnits){
            toastShow(view,"Invalid input");
        }
    }

    private void toastShow(View view,String displayText) {
        Toast bill = Toast.makeText(view.getContext(),displayText, 1000);
        bill.show();
    }

    private JSONArray getSlabRatesJson() throws JSONException {
        String slabRatesString = billDetailsDataStore.getBillDetailsString();
        JSONArray slabRatesJson=new JSONArray(slabRatesString);
        return slabRatesJson;
    }

    float calculateCost(JSONArray slabs, int units) throws JSONException {
        float billAmount = 0;
        float slabLimit;
        for(int i=0;i<slabs.length();i++){
           JSONObject slab = slabs.getJSONObject(i);
           boolean lastSlab = i == slabs.length() - 1;

           if(lastSlab)
            slabLimit= units;
           else
            slabLimit= getFloatValueForStringJson(slab, "slab");

           if(units<=slabLimit){
                float fixedAmount = getFloatValueForStringJson(slab, "fixed");
                JSONArray slots = slab.getJSONArray("slots");
                billAmount = fixedAmount + calculateSlabRate(slots, units);
                return billAmount;
           }
        }
        return billAmount;
    }

    private float getFloatValueForStringJson(JSONObject jsonObject, String stringJson) throws JSONException {
        return Float.parseFloat(jsonObject.getString(stringJson));
    }

    private float calculateSlabRate(JSONArray slots, int units) throws JSONException {
       float totalSlabAmount=0;
       float lowerRange=0;
       for(int i=0;i<slots.length();i++){
           JSONObject slot = slots.getJSONObject(i);
           float upperRange;
           boolean lastSlot = i == slots.length() - 1;

           if(lastSlot)
               upperRange=units;
           else
               upperRange = getFloatValueForStringJson(slot, "range");

           float slabRate = getFloatValueForStringJson(slot, "cost");
           totalSlabAmount+=(upperRange-lowerRange)*slabRate;
           lowerRange=upperRange;
       }
        return totalSlabAmount;
    }


    @Override
    public void OnTaskComplete(String response) {
        billDetailsDataStore.insert(response);
    }
}

