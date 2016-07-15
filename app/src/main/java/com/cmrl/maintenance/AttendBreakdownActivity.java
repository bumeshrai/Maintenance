package com.cmrl.maintenance;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prasa on 13-Jul-16.
 */
public class AttendBreakdownActivity extends AppCompatActivity {
    LinearLayout attendBreakdownLayout;
    RadioGroup radioGroup;
    Button btView;
    String getAttentionPendingURL = "/breakdown";
    Response.ErrorListener errorListener;
    Response.Listener<JSONObject> updateResponseListener;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_breakdown);
        attendBreakdownLayout = (LinearLayout) findViewById(R.id.attendBreakdownLayout);
        radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(30, 30, 30, 30);
        SaveData.editTextList.clear();
        SaveData.checkedList.clear();

        updateResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try {
                    //Log.i("value", "Response: "+jsonResponse.toString());
                    jsonResponse = jsonResponse.getJSONObject("data");
                    //Log.i("value", "Response: "+jsonResponse.toString());
                    boolean success = jsonResponse.getBoolean("success");
                    if (success)
                        Toast.makeText(AttendBreakdownActivity.this, "Updated", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(AttendBreakdownActivity.this, "Failed to update! Please Try again", Toast.LENGTH_LONG).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              /*  if (error instanceof TimeoutError) {
                    Log.i("Volley", "Error: TimeoutError  " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.i("Volley", "Error: Server Error " + error.getMessage());
                } else if (error instanceof AuthFailureError) {
                    Log.i("Volley", "Error: Auth Failure Error " + error.getMessage());
                } else if (error instanceof ParseError) {
                    Log.i("Volley", "Error: Parse Error " + error.getMessage());
                } else if (error instanceof NoConnectionError) {
                    Log.i("Volley", "Error: No Connection Error " + error.getMessage());
                } else if (error instanceof NetworkError) {
                    Log.i("Volley", "Error: NetworkError " + error.getMessage());
                }
*/
            }
        };

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 50, 0, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        btView = new Button(this);
        btView.setText("Update");
        btView.setTextColor(getResources().getColor(R.color.white));
        btView.setBackgroundResource(R.drawable.rounded_button);

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager.isAcceptingText())
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(id);
                String attendedBy = SaveData.editTextList.get(0).getText().toString();
                String repairDesc = SaveData.editTextList.get(1).getText().toString();
                if (rb != null) {
                    if (!(attendedBy.equals("")) && !(repairDesc.equals(""))) {
                        JSONObject attendBreakdownObject = new JSONObject();
                        try {
                            attendBreakdownObject.put("bd_id", id);
                            attendBreakdownObject.put("attended_by", attendedBy);
                            attendBreakdownObject.put("repair_description", repairDesc);
                            attendBreakdownObject.put("attended", "1");
                            //Log.i("value1", "AttendBreakdownobject JSON: " + attendBreakdownObject.toString());
                            BreakdownUpdateRequest worksheetRequest = new BreakdownUpdateRequest(attendBreakdownObject, "breakdown/update",
                                    updateResponseListener, errorListener);
                            //Log.i("value1", "update Request");
                            queue.add(worksheetRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(AttendBreakdownActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Select an Asset Code!!", Toast.LENGTH_SHORT).show();
            }
        });
        btView.setLayoutParams(buttonParams);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //Log.i("value1", jsonResponse.toString());
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userObj = dataArray.getJSONObject(i);
                        String org = userObj.getString("asset_code");
                        String bdId = userObj.getString("bd_id");
                        new CreateLayout(AttendBreakdownActivity.this, attendBreakdownLayout).createRadioButton(org, Integer.parseInt(bdId), radioGroup);
                        //Log.i("value1",org);
                    }
                    attendBreakdownLayout.addView(radioGroup);
                    new CreateLayout(AttendBreakdownActivity.this, attendBreakdownLayout).createEditText("Name");
                    new CreateLayout(AttendBreakdownActivity.this, attendBreakdownLayout).createEditText("Repair description");
                    attendBreakdownLayout.addView(btView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // Volley Request
        AssetMaintRequest contractorRequest = new AssetMaintRequest(getAttentionPendingURL, responseListener, errorListener);
        queue = Volley.newRequestQueue(AttendBreakdownActivity.this);
        queue.add(contractorRequest);

    }
}
