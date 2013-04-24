package com.Hackathon.EB_Bill_Calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Calculator extends Activity {

    private Button VWsubmit;
    private EditText VWfromUnits;
    private EditText VWtoUnits;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        VWsubmit = (Button) findViewById(R.id.submit);
        VWfromUnits = (EditText) findViewById(R.id.textFromUnit);
        VWtoUnits = (EditText) findViewById(R.id.textToUnits);

        VWsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                //get view inputs
                Integer fromUnits = Integer.valueOf(VWfromUnits.getText().toString());
                Integer toUnits = Integer.valueOf(VWtoUnits.getText().toString());

                //calculate bill
                int totalUnits = toUnits - fromUnits;
                JSONArray slabRatesJson = getSlabRatesJson();
                float cost = getBill(slabRatesJson, totalUnits);

                //display bill
                Toast bill = Toast.makeText(view.getContext(), String.valueOf(cost), 1000);
                bill.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONArray getSlabRatesJson() throws JSONException {

        String slabRatesString = getSlabRatesString();
        JSONArray slabRatesJson=new JSONArray(slabRatesString);
        return slabRatesJson;
    }

    float getBill(JSONArray slabs, int units) throws JSONException {
        float billAmount = 0;
        float slabLimit = 0;
        for(int i=0;i<slabs.length();i++){
           JSONObject slab = (JSONObject)slabs.get(i);
           boolean lastSlab = i == slabs.length() - 1;

           if(!lastSlab){
            slabLimit= getFloatValueForStringJson(slab, "slab");
           }
           if(lastSlab || units<slabLimit){
                float fixedAmount = getFloatValueForStringJson(slab, "fixed");
                JSONArray slots = slab.getJSONArray("slots");

                billAmount = fixedAmount+ calculateSlabRate(slots, units);
                return billAmount;
           }
        }
        return billAmount;
    }

    private float getFloatValueForStringJson(JSONObject jsonObject, String stringJson) throws JSONException {
        return Float.parseFloat(jsonObject.getString(stringJson));
    }

    private int calculateSlabRate(JSONArray slots, int units) throws JSONException {
        int totalSlabAmount=0;
        float lowerRange=0;
       for(int i=0;i<slots.length();i++){
           JSONObject slot = slots.getJSONObject(i);
           float upperRange;
           if(i==slots.length()-1){
               upperRange=units;
           }
           else{
           upperRange = getFloatValueForStringJson(slot, "range");
           }
           float slabRate = getFloatValueForStringJson(slot, "cost");
           totalSlabAmount+=(upperRange-lowerRange)*slabRate;
           lowerRange=upperRange;
       }

        return totalSlabAmount;
    }

    private String getSlabRatesString() {
        String slabRates="[\n" +
                "    {\n" +
                "        \"slab\": \"100\",\n" +
                "        \"fixed\": \"20\",\n" +
                "        \"slots\": [\n" +
                "            {\n" +
                "                \"range\": \"100\",\n" +
                "                \"cost\": \"1\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"slab\": \"200\",\n" +
                "        \"fixed\": \"20\",\n" +
                "        \"slots\": [\n" +
                "            {\n" +
                "                \"range\": \"200\",\n" +
                "                \"cost\": \"1.5\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"slab\": \"500\",\n" +
                "        \"fixed\": \"30\",\n" +
                "        \"slots\": [\n" +
                "            {\n" +
                "                \"range\": \"200\",\n" +
                "                \"cost\": \"2\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"range\": \"500\",\n" +
                "                \"cost\": \"3\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"slab\": \"max\",\n" +
                "        \"fixed\": \"40\",\n" +
                "        \"slots\": [\n" +
                "            {\n" +
                "                \"range\": \"200\",\n" +
                "                \"cost\": \"3\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"range\": \"500\",\n" +
                "                \"cost\": \"4\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"range\": \"x\",\n" +
                "                \"cost\": \"5.75\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
        return slabRates;
    }

}

