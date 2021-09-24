package com.example.bigbusiness_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    FirebaseDatabase db;
    ImageSlider mainslider;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainslider = findViewById(R.id.image_slider);
        final List<SlideModel> remoteImages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Slider").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                      for(DataSnapshot data: snapshot.getChildren())
                      {
                         remoteImages.add(new SlideModel(data.child("url").getValue().toString(),data.child("title").getValue().toString(),null));
                      }
                      mainslider.setImageList(remoteImages,null);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("LOGOUT");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getTitle().equals("LOGOUT")) ;
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}