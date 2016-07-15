package com.cmrl.maintenance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by prasa on 11-Jul-16.
 */
public class BreakdownMaintActivity extends AppCompatActivity {
    String breakdownRequestURL = "asset-code-component/?comp=csel";
    LinearLayout breakdownLayout;
    LinearLayout.LayoutParams spinnerParams;
    ArrayList<String> corridorList, stationList, equipmentList, locationList;
    List<Spinner> spinnerList;
    String assetCode = "";
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_maint);
        SaveData.editTextList.clear();
        SaveData.EQUIPMENT = "";
        SaveData.LOCATION = "";
        SaveData.STATION_NAME = "";

        breakdownLayout = (LinearLayout) findViewById(R.id.breakdownLayout);
        spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        spinnerParams.setMargins(2,10,2,10);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 50, 0, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        spinnerList = new ArrayList<>();
        editText = new EditText(this);

        corridorList = new ArrayList<>();
        corridorList.add("Select Corridor");
        stationList = new ArrayList<>();
        stationList.add("Select Station");
        equipmentList = new ArrayList<>();
        equipmentList.add("Select Equipment");
        locationList = new ArrayList<>();
        locationList.add("Select Location");


        final Button btSubmit = new Button(this);
        btSubmit.setText("Submit");
        btSubmit.setTextColor(getResources().getColor(R.color.white));
        btSubmit.setBackgroundResource(R.drawable.rounded_button);

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("Volley", "Error");
            }
        };

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager.isAcceptingText())
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                Boolean valid = true;
                assetCode = "";
                if(editText.getText().toString().length()==4) {
                    for (int i = 0; i < spinnerList.size(); i++) {
                        long id = spinnerList.get(i).getSelectedItemId();
                        if (id == 0) {
                            Toast.makeText(BreakdownMaintActivity.this, "Please " + spinnerList.get(i).getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            valid = false;
                            break;
                        }
                    }
                }
                else{
                     Toast.makeText(BreakdownMaintActivity.this, "Enter 4 digits in number field", Toast.LENGTH_SHORT).show();
                     valid = false;
                }
                if(valid){
                    generateAssetCode();
                    //Log.i("value1",assetCode);
                    JSONObject parent = new JSONObject();
                    createJSONObject(parent);
                    //Log.i("value1"," Value of JSON: " + parent.toString());
                    SaveData.EQUIPMENT = spinnerList.get(2).getSelectedItem().toString();
                    SaveData.STATION_NAME = spinnerList.get(1).getSelectedItem().toString();
                    SaveData.LOCATION = spinnerList.get(3).getSelectedItem().toString();
                    //Log.i("value1",SaveData.EQUIPMENT + SaveData.STATION_NAME + SaveData.LOCATION);
                    Intent intent = new Intent(BreakdownMaintActivity.this,CreateActivity.class);
                    intent.putExtra("parent",parent.toString());
                    intent.putExtra("urlParams","breakdown/create");
                    intent.putExtra("bdDescription",SaveData.editTextList.get(0).getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
        btSubmit.setLayoutParams(buttonParams);

        Response.Listener<String> responseListenerforBreakdown = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonAssetResponse = new JSONObject(response);
                    //Log.i("value1",jsonAssetResponse.toString());
                    JSONObject dataObject = jsonAssetResponse.getJSONObject("data");
                    JSONObject corridorObject = dataObject.getJSONObject("corridor");
                    Iterator<String> corridorkeys = corridorObject.keys();
                    while (corridorkeys.hasNext()){
                        String key = corridorkeys.next();
                        corridorList.add(corridorObject.getString(key));
                    }
                    new CreateLayout(BreakdownMaintActivity.this,breakdownLayout).createSpinner(corridorList,spinnerList);

                    JSONObject stationObject = dataObject.getJSONObject("stationCode");
                    Iterator<String> stationkeys = stationObject.keys();
                    while (stationkeys.hasNext()){
                        String key = stationkeys.next();
                        stationList.add(stationObject.getString(key));
                    }
                    new CreateLayout(BreakdownMaintActivity.this,breakdownLayout).createSpinner(stationList, spinnerList);

                    JSONObject equipmentObject = dataObject.getJSONObject("equipmentName");
                    Iterator<String> equipmentkeys = equipmentObject.keys();
                    while (equipmentkeys.hasNext()){
                        String key = equipmentkeys.next();
                        equipmentList.add(equipmentObject.getString(key));
                    }
                    new CreateLayout(BreakdownMaintActivity.this,breakdownLayout).createSpinner(equipmentList, spinnerList);

                    createNumberEditText();

                    JSONObject locationObject = dataObject.getJSONObject("location");
                    Iterator<String> locationkeys = locationObject.keys();
                    while (locationkeys.hasNext()){
                        String key = locationkeys.next();
                        locationList.add(locationObject.getString(key));
                    }
                    new CreateLayout(BreakdownMaintActivity.this,breakdownLayout).createSpinner(locationList, spinnerList);
                    new CreateLayout(BreakdownMaintActivity.this,breakdownLayout).createEditText("Description");
                    breakdownLayout.addView(btSubmit);
                    //Log.i("value1","EQUIPMENT: " +equipment);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        AssetMaintRequest breakdownRequest = new AssetMaintRequest(breakdownRequestURL,responseListenerforBreakdown,errorListener);
        RequestQueue queue = Volley.newRequestQueue(BreakdownMaintActivity.this);
        //Log.i("value1","AssetDecipher req");
        queue.add(breakdownRequest);
    }

    private void createJSONObject(JSONObject parent) {
        try {
            parent.put("asset_code",assetCode);
            parent.put("reported_by",SaveData.USER_ID);
            parent.put("bd_description",SaveData.editTextList.get(0).getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateAssetCode() {
        for(int i = 0 ; i < spinnerList.size(); i++) {
            long id = spinnerList.get(i).getSelectedItemId();
            if (i == 0 || i == 3)
                assetCode += String.format("%02d", id);
            else
                assetCode += String.format("%04d", id);
        }
        StringBuilder stringBuilder = new StringBuilder(assetCode);
        stringBuilder.insert(10,editText.getText().toString());
        assetCode = stringBuilder.toString();
        //Log.i("value1","Generated asset code" + assetCode);
    }

    void createNumberEditText() {
        //Log.i("value1", "createNumberEditText: 1");
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(17);
        editText.setHint("Number");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4)});
        editText.setHintTextColor(Color.DKGRAY);
        editText.setPadding(20, 20, 20, 20);
        editText.setBackgroundResource(R.drawable.rounded_white);
        editText.setLayoutParams(spinnerParams);
        breakdownLayout.addView(editText);
    }
}
