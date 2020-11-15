package com.ethnicindia.omkarmic.kalyanam;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethnicindia.omkarmic.kalyanam.miscellaneous.AboutUs;
import com.ethnicindia.omkarmic.kalyanam.miscellaneous.ContactUs;
import com.ethnicindia.omkarmic.kalyanam.miscellaneous.MyProfile;
import com.ethnicindia.omkarmic.kalyanam.models.Event;
import com.ethnicindia.omkarmic.kalyanam.viewHolders.EventViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrishistudio.devstudio3331.carouselview.CarouselView;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private DatabaseReference mRef;
    private RecyclerView eventView;
    private RecyclerView.LayoutManager manager;
    private CarouselView mCarousels;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = (DrawerLayout)findViewById(R.id.mDrawer);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        eventView = (RecyclerView)findViewById(R.id.events_view);
        mRef = FirebaseDatabase.getInstance().getReference().child("events");

        manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        eventView.setLayoutManager(manager);

        userName = (TextView)findViewById(R.id.user_name);
        mCarousels = (CarouselView)findViewById(R.id.main_carousels);

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.drawer_profile:
                        startActivity(new Intent(MainActivity.this, MyProfile.class));
                        break;

                    case R.id.drawer_events:
                        startActivity(new Intent(MainActivity.this, MyRegistrations.class));
                        break;

                    case R.id.drawer_about:
                        startActivity(new Intent(MainActivity.this, AboutUs.class));
                        break;

                    case R.id.drawer_contact:
                        startActivity(new Intent(MainActivity.this, ContactUs.class));
                        break;

                    default:
                        break;
                }
                mDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCarousels.setLength(4);
        mCarousels.enableDots();
        mCarousels.overlapDotsonImage();
        mCarousels.setColorActive(getResources().getColor(R.color.colorAccent));
        mCarousels.setColorInActive(getResources().getColor(R.color.colorPrimaryLight));
        FirebaseDatabase.getInstance().getReference().child("promotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        mCarousels.addPromotion(iterator.next().getValue().toString());
                    }
                    mCarousels.showCarousel(getSupportFragmentManager());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerAdapter<Event, EventViewHolder> adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.event_layout, EventViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(EventViewHolder eventViewHolder, Event event, int i) {
                eventViewHolder.setTitle(event.getTitle());
                eventViewHolder.setDate(event.getDate());
                eventViewHolder.setDetails(event.getDetails());
                eventViewHolder.setListeners(MainActivity.this, getRef(i).getKey());
            }
        };
        eventView.setAdapter(adapter);
    }

    public void toggleMenu(View view){
        mDrawer.openDrawer(GravityCompat.START);
    }

    public void viewDetails(View view){
        startActivity(new Intent(MainActivity.this, EventDetails.class));
    }

}