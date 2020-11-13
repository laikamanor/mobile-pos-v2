package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class api_transfer_class {

    private RequestQueue mQueue;
    String zxc = "";
    public void getWarehosueInventory(Activity activity){
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, zxc, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if(success){
                        String newToken = response.getString("token");
                        Toast.makeText(activity,"Login Success" ,Toast.LENGTH_SHORT).show();
                    }else{
//                            String msgError = response.getString("message");
//                            Toast.makeText(getBaseContext(), msgError, Toast.LENGTH_SHORT).show();
                    }
                    System.out.println(success);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
//            {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String>  params = new HashMap<String, String>();
//                    params.put("Content-Type", "application/json; charset=UTF-8");
////                if(!url.contains("get_token")){
////                    params.put("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMjczMzMxMiwiZXhwIjoxNjAyNzU0OTEyfQ.eyJ1c2VyX2lkIjoyfQ.Svo6UiQ7CtE2DPLbTvDUDGmXHHHLc7ZyYEwEpainGxPHz3JXS3eAc5xIyxit6nA_K3l90HEtnyzeT_gMJMT_Lg");
////                }
//                    return params;
//                }
//            };
        mQueue.add(request);
    }

}
