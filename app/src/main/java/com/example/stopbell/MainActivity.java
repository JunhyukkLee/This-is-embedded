package com.example.stopbell;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView busStopListView;
    private BusStopAdapter busStopAdapter;
    private ArrayList<BusStop> busStopsList;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private BusStop selectedBusStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // 위치 요청 설정
        createLocationRequest();

        // 위치 콜백 정의
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // 사용자의 현재 위치와 선택된 정류장 사이의 거리를 체크
                    if (selectedBusStop != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                selectedBusStop.getLatitude(), selectedBusStop.getLongitude(), results);
                        float distanceInMeters = results[0];
                        if (distanceInMeters < 300) {
                            // 사용자가 정류장과 300m 이내에 있으면 모달창을 띄웁니다.
                            runOnUiThread(() -> showArrivalDialog());
                        }
                    }
                }
            }
        };

        // ListView 초기화
        busStopListView = findViewById(R.id.busStopListView);

        // 버스 정류장 데이터를 로드합니다. 실제 앱에서는 API 호출 등을 통해 데이터를 가져올 수 있습니다.
        busStopsList = loadBusStops();

        // Adapter 초기화 및 ListView에 설정
        busStopAdapter = new BusStopAdapter(this, busStopsList);
        busStopListView.setAdapter(busStopAdapter);

        // ListView의 아이템 클릭 리스너 설정
        busStopListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedBusStop = busStopsList.get(position);
            // 사용자의 위치 업데이트 시작
            startLocationUpdates();
        });

    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 위치 업데이트 간격: 5초
        locationRequest.setFastestInterval(5000); // 가장 빠른 업데이트 간격: 5초
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void showArrivalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage("지금!");
        builder.setPositiveButton("확인", (dialog, which) -> {
            // 위치 업데이트 중단
            fusedLocationClient.removeLocationUpdates(locationCallback);
            dialog.dismiss();
        });
        builder.setOnDismissListener(dialog -> {
            // 위치 업데이트 중단
            fusedLocationClient.removeLocationUpdates(locationCallback);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLocationInDialog(Location location) {
        // AlertDialog.Builder를 사용하여 위치 정보를 표시하는 모달창 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("현재 위치");
        String locationInfo = "위도: " + location.getLatitude() + "\n경도: " + location.getLongitude();
        builder.setMessage(locationInfo);
        builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // onRequestPermissionsResult 메서드를 오버라이드하여 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 부여된 후, 위치 정보를 가져올 수 있음
        }
    }

    // 가상의 데이터 로드 함수입니다.
    private ArrayList<BusStop> loadBusStops() {
        // 여기에 실제 데이터를 로드하는 로직을 구현합니다.
        // 예시를 위해 목 데이터를 생성합니다.
        ArrayList<BusStop> list = new ArrayList<>();
        list.add(new BusStop("우리집",  37.546834915756, 127.15046447134));
        list.add(new BusStop("가천대", 37.449692542638, 127.12700692477));
        // ... 추가 정류장 데이터를 여기에 넣습니다.
        return list;
    }

    // BusStop 클래스 (버스 정류장을 나타내는 모델)
    public static class BusStop {
        private String name;
        private double latitude;
        private double longitude;

        public BusStop(String name,  double latitude, double longitude) {
            this.name = name;

            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }


        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}