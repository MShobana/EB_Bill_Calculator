package com.Hackathon.EB_Bill_Calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Calculator extends Activity implements ICallback {

    private Button VWSubmit;
    private EditText VWFromUnits;
    private EditText VWToUnits;
    private Integer fromUnits;
    private Integer toUnits;
    private BillDetailsDataStore billDetailsDataStore;
    private ICallback activityClassObject;
    private View VWTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CreateViewElements();
        CreateDatastore();
        InitializeDatabase();
        InitializeViewElements();
        activityClassObject=this;

        VWSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setFieldsFromView();
                    validateInput(view);
                    float cost = getBill(fromUnits, toUnits);
                    billDetailsDataStore.updateFromUnits(toUnits);
                    toastShow(view.getContext(), String.valueOf(cost));
                } catch (Exception e) {
                    toastShow(view.getContext(), "Invalid input");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.state:
                showStatesDialog();
                break;
            case R.id.update:
                updateBillRates();
                break;
            default:
                break;
        }
        return true;
    }

    private void showStatesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] states = getResources().getStringArray(R.array.States);
        builder.setItems(states,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedState;
                selectedState = states[i];
                billDetailsDataStore.unselectOldState();
                billDetailsDataStore.updateSelectedState(selectedState);
                ClearInputElements();
                InitializeTitle();
                InitializeFromUnits();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void ClearInputElements() {
        VWFromUnits.setText("");
        VWToUnits.setText("");
    }

    private void InitializeViewElements() {
        InitializeFromUnits();
        InitializeTitle();
    }

    private void InitializeTitle() {
        String titlePrefix = getResources().getString(R.string.title);
        String selectedState = billDetailsDataStore.getSelectedState();
        ((TextView)VWTitle).setText(titlePrefix + " " + selectedState);
    }

    private void InitializeFromUnits() {
            int fromUnits = billDetailsDataStore.getFromUnitsForSelectedState();
            if(fromUnits!=0)
            VWFromUnits.setText(Integer.toString(fromUnits));
    }

    private void updateBillRates() {
        String selectedState = billDetailsDataStore.getSelectedState();
        String selectedStateWithoutSpace = removeSpace(selectedState);
        String url = "http://guarded-badlands-3707.herokuapp.com/"+selectedStateWithoutSpace;
        new calculateAsyncTask(activityClassObject).execute(url);
    }

    private String removeSpace(String selectedState) {
        return selectedState.replace(" ","");
    }

    private void InitializeDatabase() {
        int rowsCount = billDetailsDataStore.getCount();
        if(rowsCount==0){
           updateBillRates();
        }
    }

    private void CreateDatastore() {
        billDetailsDataStore = new BillDetailsDataStore(getApplicationContext());
    }

    private void CreateViewElements() {
        VWSubmit = (Button) findViewById(R.id.submit);
        VWFromUnits = (EditText) findViewById(R.id.textFromUnit);
        VWToUnits = (EditText) findViewById(R.id.textToUnits);
        VWTitle = findViewById(R.id.titleWithState);
    }

    private void setFieldsFromView() {
        fromUnits = Integer.valueOf(VWFromUnits.getText().toString());
        toUnits = Integer.valueOf(VWToUnits.getText().toString());
    }

    private float getBill(Integer fromUnits, Integer toUnits) throws JSONException {
        int totalUnits = toUnits - fromUnits;
        JSONArray slabRatesJson = getSlabRatesJson();
        return calculateCost(slabRatesJson, totalUnits);
    }

    private void validateInput(View view) {
        if(fromUnits<0 || toUnits<0 || fromUnits>toUnits){
            toastShow(view.getContext(),"Invalid input");
        }
    }

    private void toastShow(Context context,String displayText) {
        Toast toast = Toast.makeText(context, displayText, 1000);
        toast.show();
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
        String selectedState = billDetailsDataStore.getSelectedState();
        String message="Error in updating bill rates for";
        if(response!=null){
         billDetailsDataStore.updateBillDetailsJson(response);
         message="Bill rates updated for";
        }
        toastShow(this.getApplicationContext(),message + " " + selectedState);
    }
}

