package com.example.bigbusiness_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private FirebaseDatabase db;
    private EditText name, phone, pincode, state, city, country;
    private DatabaseReference root;
    private String pin;
   private  String st,dst,ctry;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phonenumber);
        pincode = (EditText) findViewById(R.id.pincode);
        state = (EditText) findViewById(R.id.state);
        city = (EditText) findViewById(R.id.city);
        country = (EditText) findViewById(R.id.country);
        st = "";
        ctry = "";
        dst = "";

    }

    public void register(View v)
    {
        String username = name.getText().toString();
        String getphone = phone.getText().toString();
        String getpincode = pincode.getText().toString();
        String getState = state.getText().toString();
        String getcity = city.getText().toString();
        String getcountry = country.getText().toString();
        if (username.isEmpty())
        {
            name.setError("Enter Your Name");
            return;
        }
        if (getphone.isEmpty() || getphone.length() < 10) {
            if (getphone.length() < 10) {
                phone.setError("Enter Valid Phone no");
                return;
            }
            if (getphone.isEmpty()) {
                phone.setError("Please Enter Your Phone No");
                return;
            }
        }
        if (getpincode.isEmpty())
        {
            pincode.setError("Enter Your pincode");
            return;
        }
// Request a string response from the provided URL.


        if (getState.isEmpty())
        {
            state.setError("Enter Your State");
            return;
        }

        if (getcity.isEmpty())
        {
            city.setError("Enter Your City");
            return;
        }
        if (getcountry.isEmpty())
        {
            country.setError("Enter Your Country");
            return;
        }

        StoreUserData(username, getphone, getpincode, getState, getcity, getcountry);
    }

    private void StoreUserData(String username,String getphone,String getpincode,String getState,String getcity,String getcountry)
    {
        HashMap<String,String>  userMap = new HashMap<>();
        userMap.put("name",username);
        userMap.put("Phone",getphone);
        userMap.put("Pincode",getpincode);
        userMap.put("State",getState);
        userMap.put("City",getcity);
        userMap.put("Country",getcountry);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(getphone).setValue(userMap);
        Toast.makeText(this, "Registered Successfully in Database ",Toast.LENGTH_LONG).show();
        startActivity(new Intent(RegisterActivity.this,LogInActivity.class));
        finish();
    }

    public void filldetails(View view)
    {

        st = dst = ctry="";
        String pin =  pincode.getText().toString();
        String url = "http://www.postalpincode.in/api/pincode/" + pin;
        // below line is use to initialize our request queue.
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        // in below line we are creating a
        // object request using volley.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                // inside this method we will get two methods
                // such as on response method
                // inside on response method we are extracting
                // data from the json format.
                try
                {
                    JSONArray postOfficeArray = response.getJSONArray("PostOffice");
                    // we are getting data of post office
                    // in the form of JSON file.
                        JSONObject jsonObject = postOfficeArray.getJSONObject(0);
                        // inside our json array we are getting district name,
                        // state and country from our data.
                        // after getting all data we are setting this data in
                        // our text view o below line.
                        ctry = (String) jsonObject.get("Country");
                        st = (String) jsonObject.get("State");
                        dst = (String) jsonObject.get("District");
                        state.setText(st);
                        country.setText(ctry);
                        city.setText(dst);
                }
                catch (JSONException e)
                {
                    // if we gets any error then it
                    // will be printed in log cat.
                    e.printStackTrace();
                    pincode.setText("Pin code notttt  not valid");
                }
                // if the status is success we are calling this method
                // in which we are getting data from post office object
                // here we are calling first object of our json array.
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                // below method is called if we get
                // any error while fetching data from API.
                // below line is use to display an error message.
                Toast.makeText(RegisterActivity.this, "Pin code is not valid.", Toast.LENGTH_SHORT).show();
                pincode.setText("Pin code not  id d dd d d d d  d id is is is not valid");
            }
        });
        // below line is use for adding object
        // request to our request queue.
        queue.add(objectRequest);
    }
}