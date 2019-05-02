package com.example.noura.riyadh_tb.UserProfile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.StartPages.SignUp;
import com.example.noura.riyadh_tb.StartPages.Start;
import com.example.noura.riyadh_tb.model.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity {

    private DatabaseReference mDatabase;
    TextView Name, Time;
    private String user_name;
    private Integer user_time;
    private LinearLayout lay;
    private ImageView profileImage;
    RatingBar profile_ratingBar;
boolean go =true;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageView edit_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_profile);


        //edit
        edit_icon = findViewById(R.id.p_edit);
        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyProfile.this, EditMyProfile.class);
                startActivity(intent);

            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

 /*       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */


        lay = findViewById(R.id.profile_lay);
        Name= findViewById(R.id.profile_name23);
        Time =findViewById(R.id.profile_time23);

        profileImage=findViewById(R.id.service_user);

        profile_ratingBar=findViewById(R.id.UserProfile_ratingBar);
        //HEADER INFO.
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String UserID = user.getUid();
        String UserEmail = user.getEmail();
        profile_ratingBar.setEnabled(false);
        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_name = dataSnapshot.child("name").getValue(String.class);
                user_time = dataSnapshot.child("timeCredit").getValue(Integer.class);
                String url=dataSnapshot.child("photoUrl").getValue().toString();
                Name.setText(user_name);
                String time = Integer.toString(user_time);
                Time.setText(time);
if(go) {
    Glide.with(MyProfile.this).load(url.isEmpty() ? getResources().getDrawable(R.drawable.user1) : url)
            .into(profileImage);

}
                go=false;



                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Services");
                Query query=databaseReference.orderByChild("responseBy").equalTo(dataSnapshot.child("username").getValue(String.class));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter=0;
                        int rate=0;
                        int five=0;
                        int four=0;
                        int three=0;
                        int twe=0;
                        int one=0;

                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Service service=snapshot.getValue(Service.class);

                            if(service.isConfirm()==true){
                                counter++;
                                if(service.getRate()==5)
                                    five++;
                                if(service.getRate()==4)
                                    four++;
                                if(service.getRate()==3)
                                    three++;
                                if(service.getRate()==2)
                                    twe++;
                                if(service.getRate()==1)
                                    one++;
                            }

                        }
                        if((five+four+three+twe+one)!=0){
                            rate=((5*five)+(4*four)+(3*three)+(2*twe)+(1*one))/(five+four+three+twe+one);
                            profile_ratingBar.setRating(rate);}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }



        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position){
                case 0:
                    fragment = new MyProfile_Frag1();
                    break;
                case 1:
                    fragment = new MyProfile_Frag2();
                    break;
                case 2:
                    fragment = new MyProfile_Frag3();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
    public void editProfile(View view){
        Intent intent= new Intent(MyProfile.this, EditMyProfile.class);
        startActivity(intent);
    }
}
