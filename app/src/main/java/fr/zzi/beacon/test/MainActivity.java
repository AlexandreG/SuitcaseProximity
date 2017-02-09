package fr.zzi.beacon.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String BEACON_UUID = "b9407f30-f5f8-466e-aff9-25556b57fe6e";
    public static final int BEACON_MAJOR = 15150;
    public static final int BEACON_MINOR = 54609;


    private BeaconManager beaconManager;
    private Region region;

    private TextView distanceView;
    private View progressBar;
    private ImageView suitcaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distanceView = (TextView) findViewById(R.id.distance);
        progressBar = findViewById(R.id.progress_bar);
        suitcaseView = (ImageView) findViewById(R.id.suitcase);

        region = new Region("ranged region", UUID.fromString(BEACON_UUID), BEACON_MAJOR, BEACON_MINOR);

        progressBar.setVisibility(View.VISIBLE);
        suitcaseView.setVisibility(View.GONE);

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    double distance = Utils.computeAccuracy(nearestBeacon);
                    distanceView.setText(distance + "");
                    if (distance > 0) {
                        progressBar.setVisibility(View.GONE);
                        suitcaseView.setVisibility(View.VISIBLE);
                        float scale = getScaleLevel(distance);
                        suitcaseView.setScaleX(scale);
                        suitcaseView.setScaleY(scale);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        suitcaseView.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    public float getScaleLevel(double distance) {
        return (float) distance * (-0.15f) + 2;
    }
}
