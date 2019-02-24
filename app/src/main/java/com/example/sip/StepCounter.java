package com.example.sip;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepCounter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepCounter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepCounter extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private Button startButton;
    private TextView stepCountTextView;
    private int stepCount;

    private LocationManager locationManager;

    public StepCounter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepCounter.
     */
    // TODO: Rename and change types and number of parameters
    public static StepCounter newInstance(String param1, String param2) {
        StepCounter fragment = new StepCounter();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View stepCounterView = inflater.inflate(R.layout.fragment_step_counter, container, false);
        Button shareButton = stepCounterView.findViewById(R.id.shareBtn);
        ImageView heartImg = stepCounterView.findViewById(R.id.heartImg);
        startButton = stepCounterView.findViewById(R.id.startBtn);
        stepCountTextView = stepCounterView.findViewById(R.id.stepCount);

        // Start button on click listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StepService.class);
                if (StepService.isServiceRunning) {
                    intent.setAction(StepService.STOP_SERVICE);
                    detachServiceListener();
                    startButton.setText(getString(R.string.start_step));
                } else {
                    com.example.sip.stepcounter.StepCounter temp = new com.example.sip.stepcounter.StepCounter(getContext());
                    if (!temp.isAccelAvailable() && !temp.isNativeStepAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(true);
                        builder.setTitle("Error");
                        builder.setMessage("There is no suitable sensor for your device!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        stepCountTextView.setText(getString(R.string.default_step));
                        intent.setAction(StepService.START_SERVICE);
                        stepCountTextView.setText(getString(R.string.default_step));
                        stepCount = 0;
                        attachServiceListener();
                        startButton.setText(getString(R.string.stop_step));
                    }

                }
                getContext().startService(intent);
            }
        });

        // Set onclick listener on share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ask for Fine Location Service if not yet granted
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("[FINE]", "Requesting fine location");
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4
                    );
                }

                // Granted
                shareLoc();
            }

        });

        heartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        return stepCounterView;
    }

    private void shareLoc() {
        LocationManager locationManager = (LocationManager) (Objects.requireNonNull(getActivity()).
                getSystemService(LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.d("[LOCATION]", "Failed to get permission");
            return;
        }
        try {
            final Location location = getLastKnownLocation();
            if (location != null) {
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("stepdata");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int stepCount = 0;
                        for (DataSnapshot elm : dataSnapshot.getChildren()) {
                            try {
                                stepCount += elm.child("count").getValue(Integer.class);
                            } catch (NullPointerException npe) {
                                stepCount += 0;
                            }
                        }

                        if (stepCount == 0) {
                            stepCount = Integer.parseInt(stepCountTextView.getText().toString());
                        }

                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String ShareSub = "Here is my location";
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, ShareSub);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Can beat my record ? " + stepCount
                                + " steps.\n\n" + "Here is my last journey.\n\n" + uri);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to fetch database", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Log.d("[ERROR]", "Location manager could not been found");
            }
        } catch (Exception e) {
            Log.d("[EXCEPTION]", "Location permission failed");
            e.printStackTrace();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareLoc();
                } else {
                    Toast.makeText(getContext(), "Permission denied, can't access map", Toast.LENGTH_SHORT).show();
                }
            }
            default:
                Log.d("[REQ]", "Another request");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    public void onStepDetected(int stepCount) {
        this.stepCount = stepCount;
        stepCountTextView.setText(Integer.toString(this.stepCount));
    }

    private void attachServiceListener() {
        StepService.callback = this;
    }

    private void detachServiceListener() {
        StepService.callback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StepService.isServiceRunning) {
            attachServiceListener();
            startButton.setText(getString(R.string.stop_step));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (StepService.isServiceRunning) {
            detachServiceListener();
        }
    }
}
