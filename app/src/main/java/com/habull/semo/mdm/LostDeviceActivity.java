package com.habull.semo.mdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.habull.semo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LostDeviceActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backIcon;

    private boolean onoff = false;
    private Switch statusValue;
    private TextView statusName;
    private ConstraintLayout statusLayout;

    private ConstraintLayout ownerLayout;
    private TextView ownerValue;
    private ConstraintLayout emailLayout;
    private TextView emailValue;

    // MDM function
    private static LostDeviceActivity instance;

    private DatabaseHandle db;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private BroadcastReceiver receiver;
    public static boolean isLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_device);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        statusValue = findViewById(R.id.btnStatus);
        statusName = findViewById(R.id.statusName);
        statusLayout = findViewById(R.id.status);
        ownerLayout = findViewById(R.id.ownerInfo);
        ownerValue = findViewById(R.id.ownerInfoValue);
        emailLayout = findViewById(R.id.email);
        emailValue = findViewById(R.id.emailValue);

        // set status of button turn on/off
        Intent intent = getIntent();
        onoff = intent.getBooleanExtra("status", false);
        setSwitch();
        saveBtnMdm();

        // set value of owner and email info
        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        String valueOwner = sharedPref.getString("owner", "");
        if (!valueOwner.isEmpty()) {
            ownerValue.setText(valueOwner);
            ownerValue.setTextColor(Color.parseColor("#0D88C3"));
        }

        String valueEmail = sharedPref.getString("email", "");
        if (!valueEmail.isEmpty()) {
            emailValue.setText(valueEmail);
            emailValue.setTextColor(Color.parseColor("#0D88C3"));
        }

        statusValue.setOnClickListener(this);
        ownerLayout.setOnClickListener(this);
        emailLayout.setOnClickListener(this);
    }

    public void setSwitch() {
        statusValue.setChecked(onoff);

        if (onoff) {
            statusName.setText("Bật");
            statusName.setTextColor(Color.WHITE);
            statusLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#38ACFA")));
        }
        else {
            statusName.setText("Tắt");
            statusName.setTextColor(Color.parseColor("#474646"));
            statusLayout.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }
    }

    public void saveBtnMdm(){
        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("btnStatus", onoff);
        editor.apply();
    }

    public void saveOwnerInfo(String s){
        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("owner", s);
        editor.apply();
    }

    public void saveEmailInfo(String s){
        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", s);
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStatus:
                onoff = !onoff;
                setSwitch();

                if (onoff)
                    turnOnMode();

                saveBtnMdm();

                break;
            case R.id.ownerInfo:
                // show dialog
                BottomSheetDialog ownerDialog = new BottomSheetDialog(LostDeviceActivity.this);
                View tmpOwner = getLayoutInflater().inflate(R.layout.bottom_sheet_owner, null);
                ownerDialog.setContentView(tmpOwner);
                ownerDialog.show();

                TextView cancelBtn = tmpOwner.findViewById(R.id.cancelBtn);
                TextView doneBtn = tmpOwner.findViewById(R.id.doneBtn);
                EditText valueEt = tmpOwner.findViewById(R.id.ownerEt);

                SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
                String valueOwner = sharedPref.getString("owner", "");
                valueEt.setText(valueOwner);
                valueEt.setSelection(valueOwner.length());

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ownerDialog.dismiss();
                    }
                });

                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tmp = valueEt.getText().toString();

                        if (!tmp.isEmpty()) {
                            ownerValue.setText(tmp.trim());
                            ownerValue.setTextColor(Color.parseColor("#0D88C3"));
                            saveOwnerInfo(tmp.trim());
                        }
                        else {
                            ownerValue.setText(getString(R.string.introOwnerInfo));
                            ownerValue.setTextColor(Color.parseColor("#A6A6A6"));
                            saveOwnerInfo("");
                        }

                        ownerDialog.dismiss();
                    }
                });

                // auto show keyboard
                ownerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                break;
            case R.id.email:
                // show dialog
                BottomSheetDialog emailDialog = new BottomSheetDialog(LostDeviceActivity.this);
                View tmpEmail = getLayoutInflater().inflate(R.layout.bottom_sheet_email, null);
                emailDialog.setContentView(tmpEmail);
                emailDialog.show();

                TextView cancelBtn2 = tmpEmail.findViewById(R.id.cancelBtn);
                TextView doneBtn2 = tmpEmail.findViewById(R.id.doneBtn);
                EditText valueEt2 = tmpEmail.findViewById(R.id.ownerEt);

                SharedPreferences sharedPref2 = getSharedPreferences("semo", Context.MODE_PRIVATE);
                String valueEmail = sharedPref2.getString("email", "");
                valueEt2.setText(valueEmail);
                valueEt2.setSelection(valueEmail.length());

                cancelBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emailDialog.dismiss();
                    }
                });

                doneBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tmp = valueEt2.getText().toString();

                        if (!tmp.isEmpty()) {
                            emailValue.setText(tmp.trim());
                            emailValue.setTextColor(Color.parseColor("#0D88C3"));
                            saveEmailInfo(tmp.trim());
                        }
                        else {
                            emailValue.setText(getString(R.string.introEmail));
                            emailValue.setTextColor(Color.parseColor("#A6A6A6"));
                            saveEmailInfo("");
                        }

                        emailDialog.dismiss();
                    }
                });

                // auto show soft keyboard
                emailDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                break;
        }
    }

    public void turnOnMode() {
        instance = this;

        // get token of firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.i("token code", task.getResult());
                    }
                });

        // request admin permission
        DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName compName = new ComponentName(getApplicationContext(), OwnerReceiver.class);

        if (!dpm.isAdminActive(compName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Press active button.");

            startActivity(intent);
        }

        // request permission accessibility
        startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));

        // create request update location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        db.addRecord(latitude, longitude);
                    }
                }
            }
        };

        // read location from database sqli to write in logcat
        db = new DatabaseHandle(this);
        ArrayList<String> test = db.getAllLocation();
        for (int i = 0; i < test.size(); i++)
            Log.i("location db", test.get(i));

        // check and request location, camera and storage permissions
        checkPermission();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new BootBroadcast();
        registerReceiver(receiver, filter);
    }

    public static LostDeviceActivity getInstance() {
        return instance;
    }

    public void getLocation() {
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        else {
            // get location here if permission granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        db.addRecord(latitude, longitude);
                        Log.i("location", String.valueOf(latitude) + " " + String.valueOf(longitude));
                    } else
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000: {
                // If request is accepted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                db.addRecord(latitude, longitude);
                            } else
                                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
                } else    // if request is denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    public String getInfoLocation(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0);

        return address;
    }

    public void sendEmail() throws Exception {
        // get info location found from database
        db = new DatabaseHandle(this);
        ArrayList<String> test = db.getAllLocation();
        String tmp = test.get(0);
        String[] tmpSplit = tmp.split(",");
        double latituteFounded = Double.parseDouble(tmpSplit[1]);
        double longtitudeFounded = Double.parseDouble(tmpSplit[2]);

        // set properties of email
        String receiverEmail = emailValue.getText().toString();
        String subject = "Thông báo thông tin thiết bị bị mất của bạn";
        String message = "Xin chào, " +
                "\n\nThiết bị của bạn được tìm thấy tại địa chỉ " + getInfoLocation(latituteFounded, longtitudeFounded) + "." +
                "\n\nẢnh được chụp tự động bởi camera trước và sau của thiết bị cũng được đính kèm trong email." +
                "\n\nSemo chúc bạn sớm tìm được thiết bị bị mất của mình." +
                "\nHabull - Người sáng lập Semo trên Android";

        SendMail sendMail = new SendMail(this, receiverEmail, subject, message);
        sendMail.execute();
    }
}