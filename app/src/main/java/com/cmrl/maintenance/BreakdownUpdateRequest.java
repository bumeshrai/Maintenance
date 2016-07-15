package com.cmrl.maintenance;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by prasa on 13-Jul-16.
 */
public class BreakdownUpdateRequest extends JsonObjectRequest{
    private static final String LOGIN_REQUEST_URL
            = "http://cmrlvent.co.in/assetMaint/api/web/";

    public BreakdownUpdateRequest(JSONObject parent, String urlParams, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        super(Request.Method.PUT, LOGIN_REQUEST_URL + urlParams, parent, responseListener, errorListener);
        //Log.i("here", "Reached WSRequest");
    }
}
