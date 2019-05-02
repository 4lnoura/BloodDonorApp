package com.example.noura.riyadh_tb.Main;


import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
import com.example.noura.riyadh_tb.model.RecommendationServices;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PublishService extends AppCompatActivity {
    // FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

    private static final String TAG = "PublishService";
    private EditText titleEditText, descriptionEditText, usernameEditText;
    private Spinner CategorySpinner;
    private RadioGroup TypeRadioGroup;
    private RadioButton SpecificP;
    private Button button_publish, button_location;
    double curentSerivceXLocation;
    double curentSerivceYLocation;
    View dialogView1;
    AlertDialog dialogView;
    ImageView image;
    String Category;
     ArrayList<RecommendationServices> list;
    /***/
    ArrayList<Service> services;


    //Declare Firebase Database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String issuedBy, Email;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_service);
        setRecommendation();
        CategorySpinner = findViewById(R.id.p_spinner);


        final Intent intent = getIntent();
        String X = intent.getStringExtra("XLocation");
        String Y = intent.getStringExtra("YLocation");
        if (X != null) {
            curentSerivceXLocation = Double.parseDouble(X);
            curentSerivceYLocation = Double.parseDouble(Y);
        } else {
            userLocation();
        }
        if (intent.getStringExtra("title") != null) {
            titleEditText = findViewById(R.id.p_title);
            titleEditText.setText(intent.getStringExtra("title"));
            descriptionEditText = (EditText) findViewById(R.id.p_des);

            descriptionEditText.setText(intent.getStringExtra("Description"));
            String sCategory = intent.getStringExtra("Category");
            String category[] = {"خدمات فنية", "خدمات السيارات", "خدمات تعلمية",
                    "خدمات منزلية", "خدمات الترجمة", "خدمات الحاسب", "خدمات اخرى "};

            int moveList = 0;
            for (moveList = 0; moveList < 7; moveList++) {
                category = new String[]{"خدمات فنية", "خدمات السيارات", "خدمات تعلمية",
                        "خدمات منزلية", "خدمات الترجمة", "خدمات الحاسب", "خدمات اخرى"};
                if (category[moveList].equals(sCategory))
                    break;
            }
            CategorySpinner.setSelection(moveList);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = user.getUid();
        Email = user.getEmail();

        /*******************************/


        /****/


        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                issuedBy = dataSnapshot.child("username").getValue(String.class);
                Log.d(TAG, "Name: " + issuedBy);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        AddService();





         image = findViewById(R.id.recomation);



        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Recommendation(list);
            }

        });














    }
    public void AddService() {


        titleEditText = (EditText) findViewById(R.id.p_title);
        descriptionEditText = (EditText) findViewById(R.id.p_des);
        usernameEditText = (EditText) findViewById(R.id.p_username);
        CategorySpinner = findViewById(R.id.p_spinner);
        TypeRadioGroup = findViewById(R.id.TypeRadioGroup);
        button_location = findViewById(R.id.p_location);
        button_publish = findViewById(R.id.publish_button);


        button_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieves user inputs
                final String Title = titleEditText.getText().toString().trim();
                final String Description = descriptionEditText.getText().toString().trim();
                final String username = usernameEditText.getText().toString().trim();
                final String Category = CategorySpinner.getSelectedItem().toString();
                boolean ty = TypeRadioGroup.getCheckedRadioButtonId() == R.id.ForOne;


                if (Title.isEmpty()) {
                    titleEditText.setError("فضلا ادخل العنوان");
                    titleEditText.requestFocus();
                    return;
                }
                if (Description.isEmpty()) {
                    descriptionEditText.setError("فضلاً ادخل الوصف");
                    descriptionEditText.requestFocus();
                    return;
                }


                final String type;
                if (ty == true) {

                    type = "One";

                    if (username.isEmpty()) {
                        usernameEditText.setError("فضلا اكتب اسم المستخدم");
                        usernameEditText.requestFocus();
                        return;
                    }

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    Query query = reference.orderByChild("username").equalTo(username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                usernameEditText.setError("اسم المستخدم خاطئ");
                                usernameEditText.requestFocus();
                                return;
                            } else {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    User user = snapshot.getValue(User.class);

                                    if (user.getEmail().equals(Email)) {

                                        usernameEditText.setError("اختر اسم مستخدم اخر");
                                        usernameEditText.requestFocus();
                                        return;
                                    } else {
                                        writeNewUser(Title, issuedBy, type, Category, Description, username);
                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    type = "All";
                    writeNewUser(Title, issuedBy, type, Category, Description, username);
                }


//                writeNewUser(Title, issuedBy, type, Category, Description,username);


            }
        });

    }

    //end method
    private void setRecommendation() {

         list = new ArrayList<RecommendationServices>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference().child("Services").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<RecommendationServices>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {


                        Service s = snapshot.getValue(Service.class);

                        double XLocationService = snapshot.child("Location").child("XLocation").getValue(Double.class);
                        double YLocationService = snapshot.child("Location").child("YLocation").getValue(Double.class);
                        list.add(new RecommendationServices(s.getTitle(), s.getCategory(), s.getDescreption(), XLocationService, YLocationService));
                    } catch (Exception e) {

                    }


                }


                /*   String u= findLocation(24.699,46.6832);
                    Toast.makeText(PublishService.this,"  "+u,Toast.LENGTH_SHORT).show();

*/
            //    Recommendation(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void writeNewUser(String Title, String issuedBy, String type, String Category,
                              String Description, String username) {


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat Time = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        final String dateP = formatter.format(date);
        final String TimeP = Time.format(date);
        //  System.out.println(dateP);
        //System.out.println(TimeP);


        final Service s = new Service(Title, issuedBy, type, Category, Description, TimeP, username, dateP, "");


        try {


            new FirebaseTransaction(this).child("Services").push().setValue(s);


            //id
            FirebaseDatabase.getInstance().getReference().child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        try {
                            if (service.getIssuedBy().equals(s.getIssuedBy()) && service.getIssuedTime().equals(s.getIssuedTime()) && service.getDate().equals(s.getDate())) {
                                Log.v("Service Title", service.getTitle());
                                String key = snapshot.getKey();
                                service.setID(key);
                                snapshot.getRef().setValue(service);


                                HashMap<String, Double> Xupdate = new HashMap<String, Double>();
                                Xupdate.put("XLocation", curentSerivceXLocation);
                                HashMap<String, Double> Yupdate = new HashMap<String, Double>();
                                Yupdate.put("YLocation", curentSerivceYLocation);


                                String t = snapshot.getKey();
                                DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t).child("Location");
                                firebaseDel.updateChildren((HashMap) Xupdate);
                                firebaseDel.updateChildren((HashMap) Yupdate);
                            }
                        } catch (Exception E) {

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Toast.makeText(PublishService.this, "Your Service Published Successfully!",
                    Toast.LENGTH_SHORT).show();


            //end assaign id

            Intent intent = new Intent(PublishService.this, Timeline.class);
            startActivity(intent);
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(PublishService.this, "Error While Adding the Service!",
                    Toast.LENGTH_SHORT).show();
        }


    }//end method


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?

        final View P_text_username = findViewById(R.id.p_text_username);

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.ForAll:
                if (checked)

                    P_text_username.setVisibility(View.GONE);
                // Pirates are the best
                break;
            case R.id.ForOne:
                if (checked)
                    // Ninjas rule

                    P_text_username.setVisibility(View.VISIBLE);
                break;
        }
    }

    ///////////////////////////////////////////////////*
    private void Recommendation(final ArrayList<RecommendationServices> services) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String UserID;
        UserID = user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<RecommendationServices> LocServiceList = FindServicesLocations(services,
                        dataSnapshot.child("Location").child("XLocation").getValue(Double.class),
                        dataSnapshot.child("Location").child("YLocation").getValue(Double.class));

                if (LocServiceList.size() > 0) {

                    if (LocServiceList.size() > 4) {

                        String Top = FindTheTopServices(LocServiceList);

                        ArrayList<RecommendationServices> TopList = new ArrayList<>();
                        ArrayList<RecommendationServices> NoTopList = new ArrayList<>();
                        int j = LocServiceList.size();
                        for (int i = 0; i < j; i++) {
                            if (LocServiceList.get(i).getCategory().equals(Top)) {
                                TopList.add(LocServiceList.get(i));
                            } else
                                NoTopList.add(LocServiceList.get(i));
                        }

                        boolean ok = true;
                        while (ok) {
                            if (TopList.size() > 4) {

                                DisplayRecommendedServices(TopList);

                                ok = false;
                            } else {

                                int size = NoTopList.size();
                                ArrayList<RecommendationServices> SECLocServiceList = new ArrayList<>();

                                for (int i = 0; i < size; i++)
                                    SECLocServiceList.add(NoTopList.get(i));


                                String SECTop = FindTheTopServices(SECLocServiceList);

                                int k = SECLocServiceList.size();

                                NoTopList.clear();

                                for (int i = 0; i < k; i++) {
                                    if (SECLocServiceList.get(i).getCategory().equals(SECTop)) {
                                        TopList.add(SECLocServiceList.get(i));
                                    } else
                                        NoTopList.add(SECLocServiceList.get(i));
                                }


                            }
                        }
                        //DisplayRecommendedServices(TopList);

                    } else {
                        String Top = FindTheTopServices(LocServiceList);
                        ArrayList<RecommendationServices> TopList = new ArrayList<>();
                        ArrayList<RecommendationServices> NoTopList = new ArrayList<>();
                        int size = LocServiceList.size();
                        for (int i = 0; i < size; i++) {
                            if (LocServiceList.get(i).getCategory().equals(Top)) {
                                TopList.add(LocServiceList.get(i));
                            } else
                                NoTopList.add(LocServiceList.get(i));
                        }
                        while (true) {
                            if (TopList.size() == size) {
                                DisplayRecommendedServices(TopList);
                                return;
                            } else {

                                ArrayList<RecommendationServices> SECLocServiceList = new ArrayList<>();
                                int k = NoTopList.size();
                                for (int i = 0; i < k; i++)
                                    SECLocServiceList.add(NoTopList.get(i));

                                String SECTop = FindTheTopServices(SECLocServiceList);

                                int g = SECLocServiceList.size();
                                NoTopList.clear();

                                for (int i = 0; i < g; i++) {
                                    if (SECLocServiceList.get(i).getCategory().equals(SECTop)) {
                                        TopList.add(SECLocServiceList.get(i));
                                    } else
                                        NoTopList.add(SECLocServiceList.get(i));
                                }


                            }
                        }
                    }

                } else {



                        final AlertDialog.Builder optionDialog = new AlertDialog.Builder(PublishService.this);
                    dialogView = setting();

                    TextView three = (TextView) dialogView.findViewById(R.id.three);
                    three.setText("لايوجد خدمات في منطقتك");



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /****/

    }


    private void DisplayRecommendedServices(ArrayList<RecommendationServices> s) {

        final String category[] = {"خدمات فنية", "خدمات السيارات", "خدمات تعلمية",
                "خدمات منزلية", "خدمات الترجمة", "خدمات الحاسب", "خدمات اخرى "};


        final AlertDialog.Builder optionDialog = new AlertDialog.Builder(this);
    dialogView = setting();
        final ArrayList<RecommendationServices> services = s;

        setView(dialogView,s);

       /* dialogView.findViewById(R.id.Accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
*/

        /*
//Toast.makeText(PublishService.this,"size "+s.size(),Toast.LENGTH_SHORT).show();
        TextView Top1, Top2, Top3, Top4, Top5;

        Top1 = findViewById(R.id.ser1);
        Top2 = findViewById(R.id.ser2);
        Top3 = findViewById(R.id.ser3);
        Top4 = findViewById(R.id.ser4);
        Top5 = findViewById(R.id.ser5);

        if (services.size() > 0)
            Top1.setText("1." + services.get(0).getTitle());
        if (services.size() > 1)
            Top2.setText("2." + services.get(1).getTitle());
        if (services.size() > 2)
            Top3.setText("3." + services.get(2).getTitle());
        if (services.size() > 3)
            Top4.setText("4." + services.get(3).getTitle());
        if (services.size() > 4)
            Top5.setText("5." + services.get(4).getTitle());*/

        dialogView.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleEditText.setText(services.get(0).getTitle());
                descriptionEditText.setText(services.get(0).getDescription());
                String cate = services.get(0).getCategory();
                int i;
                for (i = 0; i < 7; i++) {
                    if (category[i].equals(cate))
                        break;
                }
                CategorySpinner.setSelection(i);
                dialogView.dismiss();
            }
        });

        dialogView.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleEditText.setText(services.get(1).getTitle());
                descriptionEditText.setText(services.get(1).getDescription());
                String cate = services.get(1).getCategory();
                int i;
                for (i = 0; i < 10; i++) {
                    if (category[i].equals(cate))
                        break;
                }
                CategorySpinner.setSelection(i);
                dialogView.dismiss();
            }
        });

        dialogView.findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleEditText.setText(services.get(2).getTitle());
                descriptionEditText.setText(services.get(2).getDescription());
                String cate = services.get(2).getCategory();
                int i;
                for (i = 0; i < 10; i++) {
                    if (category[i].equals(cate))
                        break;
                }
                CategorySpinner.setSelection(i);
                dialogView.dismiss();
            }
        });

        dialogView.findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleEditText.setText(services.get(3).getTitle());
                descriptionEditText.setText(services.get(3).getDescription());
                String cate = services.get(3).getCategory();
                int i;
                for (i = 0; i < 10; i++) {
                    if (category[i].equals(cate))
                        break;
                }
                CategorySpinner.setSelection(i);
                dialogView.dismiss();
            }
        });

        dialogView.findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleEditText.setText(services.get(4).getTitle());
                descriptionEditText.setText(services.get(4).getDescription());
                String cate = services.get(4).getCategory();
                int i;
                for (i = 0; i < 10; i++) {
                    if (category[i].equals(cate))
                        break;
                }
                CategorySpinner.setSelection(i);
                dialogView.dismiss();
            }
        });


    }

    public String findLocation(double x, double y) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = " ";
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    x, y, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");//adress
                }

                sb.append(address.getSubLocality()).append("\n");

                result = sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }

        return result;
    }

    private ArrayList<RecommendationServices> FindServicesLocations(ArrayList<RecommendationServices> list, double userLocationX, double userLocationY) {
        ArrayList<RecommendationServices> LocServiceList = new ArrayList<>();

        int size = list.size();

        String subLocationUser = findLocation(userLocationX, userLocationY);

        for (int i = 0; i < size; i++) {
            String subLocationService = findLocation(list.get(i).getXLocation(), list.get(i).getYLocation());
            try {


                if (subLocationUser.equals(subLocationService)) {
                    //Toast.makeText(PublishService.this,"yse",Toast.LENGTH_SHORT).show();
                    LocServiceList.add(list.get(i));
                } else {
                    // Toast.makeText(PublishService.this,"No",Toast.LENGTH_SHORT).show();

                }
            } catch (Exception E) {
                Toast.makeText(PublishService.this, "  " + E.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

//IF NO [null] Dont Display any things.
        return LocServiceList;
    }

    private String FindTheTopServices(ArrayList<RecommendationServices> list) {

        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        String[] Categories = new String[7];
        Categories[0] = "خدمات السيارات";
        Categories[1] = "خدمات فنية";
        Categories[2] = "خدمات تعلمية";
        Categories[3] = "خدمات منزلية";
        Categories[4] = "خدمات الترجمة";
        Categories[5] = "خدمات الحاسب";
        Categories[6] = "خدمات اخرى";
        int k = 0;
        for (int i = 0; i < 7; i++) {
            int count = 0;
            for (int j = 0; j < list.size(); j++) {
                if (Categories[i].equals(list.get(j).getCategory())) {
                    count++;
                }
            }
            counts.put(Categories[i], count);
            k++;
        }


        //Here find the max value
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {

            if (maxEntry == null
                    || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }


        return maxEntry.getKey();
    }

    public void serviceLoc(View view) {


        Intent intent = new Intent(PublishService.this, ServiceLocation.class);
        titleEditText = (EditText) findViewById(R.id.p_title);
        descriptionEditText = (EditText) findViewById(R.id.p_des);
        CategorySpinner = findViewById(R.id.p_spinner);

        intent.putExtra("title", titleEditText.getText().toString().trim());
        intent.putExtra("Description", descriptionEditText.getText().toString().trim());
        Category = CategorySpinner.getSelectedItem().toString();

        intent.putExtra("Category", Category);

        startActivity(intent);


    }

    public void userLocation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String UserID;
        UserID = user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    curentSerivceXLocation = dataSnapshot.child("Location").child("XLocation").getValue(Double.class);
                    curentSerivceYLocation = dataSnapshot.child("Location").child("YLocation").getValue(Double.class);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private AlertDialog setting(){
        //ViewGroup viewGroup = viewGroup.findViewById();

        //then we will inflate the custom alert dialog xml that we created

         dialogView1= LayoutInflater.from(this).inflate(R.layout.recommendation_dialog, null, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView1);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //finally creating the alert dialog and displaying it
        return alertDialog;
    }

    private void setView(AlertDialog dialogView,ArrayList<RecommendationServices>s) {
int size=s.size();

if(size>0) {
    TextView one = (TextView) dialogView.findViewById(R.id.one);
    one.setText("1- "+s.get(0).getTitle());
}
if(size>1) {
    TextView twe = (TextView) dialogView.findViewById(R.id.two);
    twe.setText("2- "+s.get(1).getTitle());
}
if(size>2) {
    TextView three = (TextView) dialogView.findViewById(R.id.three);
    three.setText("3- "+s.get(2).getTitle());
}
if(size>3) {
    TextView four = (TextView) dialogView.findViewById(R.id.four);
    four.setText("4- "+s.get(3).getTitle());
}
if(size>4) {
    TextView five = (TextView) dialogView.findViewById(R.id.five);
    five.setText("5- "+s.get(4).getTitle());
}



    }
}
