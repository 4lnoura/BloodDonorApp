package com.example.noura.riyadh_tb.Main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyRequestDetails extends AppCompatActivity implements OnMapReadyCallback {

    TextView title_MR,desc_MR,type_MR,responseBy_MR;
    View RejectView,StateTitle;
    CardView cardView;
    Bundle mapViewBundle ;
    private MapView mapView;
    private GoogleMap gmap;
    private double XLocationService;
    private double YLocationService;
    private String ServicName;
    private String ID;
    private DatabaseReference reference;
    private String MAP_VIEW_BUNDLE_KEY = "AIzaSyBfSDzhviNjpouTwzB1_SkLrUvHQ4yG0ng";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request_details);
         cardView = findViewById(R.id.card);
        title_MR=findViewById(R.id.title_MyRequest);
        desc_MR=findViewById(R.id.Desc_MyRequest);
        type_MR=findViewById(R.id.Type_MyRequest);
        responseBy_MR=findViewById(R.id.responseBy_MyRequest);
        StateTitle=findViewById(R.id.state);
        mapView = findViewById(R.id.map_view);

        Bundle mapViewBundle = null;

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);


        mapView.getMapAsync(this);
        final Intent intant = getIntent();


        cardView.setRadius(50);

        final Intent intent=getIntent();

        title_MR.setText(intent.getStringExtra("Title"));
        desc_MR.setText(intent.getStringExtra("Disciption"));
        type_MR.setText(intent.getStringExtra("category"));
        responseBy_MR.setText(intent.getStringExtra("responseBy"));
        RejectView=findViewById(R.id.Liner_Of_reject);

        String state=intent.getStringExtra("state");
        ID=intent.getStringExtra("id");


        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
      /*  StepBean stepBean0 = new StepBean("接单",1);
        StepBean stepBean1 = new StepBean("打包",1);
        StepBean stepBean2 = new StepBean("出发",1);
        StepBean stepBean3 = new StepBean("送单",0);
        StepBean stepBean4 = new StepBean("完成",-1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);
        stepsBeanList.add(stepBean4);
*/

        StepBean stepBean0 ;
        StepBean stepBean1 ;
        StepBean stepBean2 ;
        StepBean stepBean3 ;


        switch (state){

            case "Published" :
                StateTitle.setVisibility(View.VISIBLE);
                stepBean0 = new StepBean("منشور",0);
                stepBean1 = new StepBean("مقبوله",-1);
                stepBean2 = new StepBean("قيد التنفيذ",-1);
                stepBean3 = new StepBean("تم التوصيل",-1);
                stepsBeanList.add(stepBean0);
                stepsBeanList.add(stepBean1);
                stepsBeanList.add(stepBean2);
                stepsBeanList.add(stepBean3);
                setpview5
                        .setStepViewTexts(stepsBeanList)//总步骤
                        .setTextSize(12)//set textSize
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.holo_blue_bright))//设置StepsViewIndicator完成线的颜色
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsViewIndicator未完成线的颜色
                        .setStepViewComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.black))//设置StepsView text完成线的颜色
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsView text未完成线的颜色
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.accept))//设置StepsViewIndicator CompleteIcon
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.attention1))//设置StepsViewIndicator DefaultIcon
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.publishicon));//设置StepsViewIndicator AttentionIcon
                break;

            case "Rejected":
                StateTitle.setVisibility(View.VISIBLE);
                RejectView.setVisibility(View.VISIBLE);

                break;


            case "Accepted":
                StateTitle.setVisibility(View.VISIBLE);
                stepBean0 = new StepBean("منشور",1);
                stepBean1 = new StepBean("مقبوله",0);
                stepBean2 = new StepBean("قيد التنفيذ",-1);
                stepBean3 = new StepBean("تم التوصيل",-1);
                stepsBeanList.add(stepBean0);
                stepsBeanList.add(stepBean1);
                stepsBeanList.add(stepBean2);
                stepsBeanList.add(stepBean3);
                setpview5
                        .setStepViewTexts(stepsBeanList)//总步骤
                        .setTextSize(12)//set textSize
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.holo_blue_bright))//设置StepsViewIndicator完成线的颜色
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsViewIndicator未完成线的颜色
                        .setStepViewComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.black))//设置StepsView text完成线的颜色
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsView text未完成线的颜色
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.accept))//设置StepsViewIndicator CompleteIcon
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.attention1))//设置StepsViewIndicator DefaultIcon
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.accept1));//设置StepsViewIndicator AttentionIcon

                break;


            case "Preparing":
                StateTitle.setVisibility(View.VISIBLE);
                stepBean0 = new StepBean("منشور",1);
                stepBean1 = new StepBean("مقبوله",1);
                stepBean2 = new StepBean("قيد التنفيذ",0);
                stepBean3 = new StepBean("تم التوصيل",-1);
                stepsBeanList.add(stepBean0);
                stepsBeanList.add(stepBean1);
                stepsBeanList.add(stepBean2);
                stepsBeanList.add(stepBean3);
                setpview5
                        .setStepViewTexts(stepsBeanList)//总步骤
                        .setTextSize(12)//set textSize
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.holo_blue_bright))//设置StepsViewIndicator完成线的颜色
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsViewIndicator未完成线的颜色
                        .setStepViewComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.black))//设置StepsView text完成线的颜色
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsView text未完成线的颜色
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.accept))//设置StepsViewIndicator CompleteIcon
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.attention1))//设置StepsViewIndicator DefaultIcon
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.preparing));//设置StepsViewIndicator AttentionIcon
                break;

            case "Delivered":
                StateTitle.setVisibility(View.VISIBLE);
                stepBean0 = new StepBean("منشور",1);
                stepBean1 = new StepBean("مقبوله",1);
                stepBean2 = new StepBean("قيد التنفيذ",1);
                stepBean3 = new StepBean("تم التوصيل",1);
                stepsBeanList.add(stepBean0);
                stepsBeanList.add(stepBean1);
                stepsBeanList.add(stepBean2);
                stepsBeanList.add(stepBean3);
                setpview5
                        .setStepViewTexts(stepsBeanList)//总步骤
                        .setTextSize(12)//set textSize
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.holo_blue_bright))//设置StepsViewIndicator完成线的颜色
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsViewIndicator未完成线的颜色
                        .setStepViewComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, android.R.color.black))//设置StepsView text完成线的颜色
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(MyRequestDetails.this, R.color.LineColore))//设置StepsView text未完成线的颜色
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.accept))//设置StepsViewIndicator CompleteIcon
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.attention1))//设置StepsViewIndicator DefaultIcon
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(MyRequestDetails.this, R.drawable.tracking));//设置StepsViewIndicator AttentionIcon

                break;
default:
    TextView Titlename=findViewById(R.id.responseBy);
    Titlename.setText("ناشر الطلب: ");
    TextView vlaueofText=findViewById(R.id.responseBy_MyRequest);
    vlaueofText.setText(intent.getStringExtra("IssuedBy"));
    HorizontalStepView viewOfLine=findViewById(R.id.step_view);

    viewOfLine.setVisibility(View.INVISIBLE);

    CardView cardView= findViewById(R.id.card);
// Gets the layout params that will allow you to resize the layout
    ViewGroup.LayoutParams params = cardView.getLayoutParams();
// Changes the height and width to the specified *pixels*
    params.height = 850;
    cardView.setLayoutParams(params);

break;

        }








    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        gmap.setMinZoomPreference(12);


        reference = FirebaseDatabase.getInstance().getReference().child("Services"); // change from db
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);
                        String ids = p.getID();
                        Boolean y;
                        if (ids.equals(ID)) {
                            ServicName=p.getTitle();

                            XLocationService = dataSnapshot1.child("Location").child("XLocation").getValue(Double.class);
                            YLocationService= dataSnapshot1.child("Location").child("YLocation").getValue(Double.class);

                            LatLng ny = new LatLng(XLocationService, YLocationService);

                            BitmapDescriptor iconSer = BitmapDescriptorFactory.fromResource(R.drawable.serivcemap);

                            gmap.addMarker(new MarkerOptions().position(ny).title(" خدمة " + ServicName).icon(iconSer));

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ny, 14);

                            gmap.animateCamera(cameraUpdate);
                        }
                    } catch (Exception ignored) {
                        Toast.makeText(MyRequestDetails.this, ignored.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyRequestDetails.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
