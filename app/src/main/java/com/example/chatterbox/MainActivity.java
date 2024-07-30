package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ImageButton searchButton,profileButton,chatButton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    SettingsFragment settingsFragment;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();
        searchButton = findViewById(R.id.btnSearch);
        bottomNavigationView = findViewById(R.id.bottomBar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,chatFragment,"chat_fragment").commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.getItemId()==R.id.btnChat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,chatFragment).commit();
                }
                else if(item.getItemId()==R.id.btnProfile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,profileFragment).commit();
                }
                else if(item.getItemId()==R.id.btnSettings){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,settingsFragment).commit();
                }
                return true;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        openPromt();
        return;
    }

    void openPromt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Attention!");
        builder.setIcon(R.drawable.icon_notification);
        builder.setMessage("Do you want to Exit?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}