package com.Hackathon.EB_Bill_Calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                String billAmount = "";
                String units = "";
                Integer fromUnits =Integer.valueOf( VWfromUnits.getText().toString());
                Integer toUnits = Integer.valueOf(VWtoUnits.getText().toString());

                int totalUnits = toUnits - fromUnits;
                String url = "http://tneb-billing.herokuapp.com/?units=" + totalUnits + "&mobile=1";
                new calculateAsyncTask().execute(url,view.getContext());
            }
        });

    }
}
