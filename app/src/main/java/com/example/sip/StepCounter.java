package com.example.sip;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepCounter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepCounter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepCounter extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button startButton;
    private TextView stepCountTextView;
    private int stepCount;

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
//                // Ask for Foreground Service if not yet granted
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.FOREGROUND_SERVICE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.FOREGROUND_SERVICE}, 1
//                    );
//
//                }
//                // Ask for Vibrate if not yet granted
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.VIBRATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.VIBRATE}, 2
//                    );
//
//                }

                // Granted
                Intent intent = new Intent(getContext(), StepService.class);
                if (StepService.isServiceRunning) {
                    intent.setAction(StepService.STOP_SERVICE);
                    detachServiceListener();
                    startButton.setText(getString(R.string.start_step));
                } else {
                    stepCountTextView.setText(getString(R.string.default_step));
                    intent.setAction(StepService.START_SERVICE);
                    stepCountTextView.setText(getString(R.string.default_step));
                    stepCount = 0;
                    attachServiceListener();
                    startButton.setText(getString(R.string.stop_step));
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
                getSystemService(Context.LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.d("[LOCATION]", "Failed to get permission");
            return;
        }
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                Log.d("URI", uri);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Here is my location";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            } else {
                Log.d("[ERROR]", "Location manager could");
            }
        } catch (Exception e) {
            Log.d("[EXCEPTION]", "Location permission failed");
            e.printStackTrace();
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onStepDetected(int stepCount) {
        this.stepCount = stepCount;
        stepCountTextView.setText(Integer.toString(this.stepCount));
    }

    public void resetStartButton() {
        startButton.setText(getString(R.string.start_step));
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
