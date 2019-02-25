package com.example.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sip.stepcounter.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class History extends Fragment {
    public static final String HISTORY_REFRESH_EVENT = "history-refresh";
    private static final String TAG_LOG = "[HISTORY]";
    private List<HistoryModel> historyContent = new ArrayList<>();
    private RVAdapter adapter;
    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateHistoryList();
        }
    };

    public History() {

    }

    public void updateHistoryList() {
        Log.d(TAG_LOG,"History update job received...");
        DatabaseReference ref = Database.getInstance().getStepData();
        historyContent.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot elm : dataSnapshot.getChildren()) {
                    String date = elm.getKey();
                    int stepCount, target;

                    try {
                        stepCount = elm.child("count").getValue(Integer.class);
                    } catch (NullPointerException npe) {
                        stepCount = 0;
                    }
                    try {
                        target = elm.child("target").getValue(Integer.class);
                    } catch (NullPointerException npe) {
                        target = 0;
                    }

                    historyContent.add(new HistoryModel(date, stepCount, target));
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, null);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        adapter = new RVAdapter(getActivity(), historyContent);

        updateHistoryList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(msgReceiver,
                new IntentFilter(HISTORY_REFRESH_EVENT));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).unregisterReceiver(msgReceiver);
        super.onPause();
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.HistoryViewHolder> {

        private List<HistoryModel> dataSource;
        private Context c;

        RVAdapter(Context c, List<HistoryModel> dataArgs) {
            this.c = c;
            this.dataSource = dataArgs;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(c).inflate(R.layout.history_items, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int position) {
            HistoryModel current = dataSource.get(position);
            String stepText = Integer.toString(current.stepCount) + " steps";
            historyViewHolder.dateView.setText(formatDate(current.date));
            historyViewHolder.stepView.setText(stepText);
            if ((current.target > 0) && (current.stepCount >= current.target)) {
                historyViewHolder.goalReachedIcon.setVisibility(ImageView.VISIBLE);
            } else {
                historyViewHolder.goalReachedIcon.setImageResource(R.drawable.wrong);
            }
        }

        String formatDate(String date) {
            String[] splittedDate = date.split("-");
            return splittedDate[0] + " " +
                    new DateFormatSymbols().getMonths()[Integer.parseInt(splittedDate[1]) - 1]
                    + " " + splittedDate[2];
        }

        @Override
        public int getItemCount() {
            return dataSource.size();
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView dateView;
            TextView stepView;
            ImageView goalReachedIcon;

            HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                dateView = itemView.findViewById(R.id.date);
                stepView = itemView.findViewById(R.id.steps);
                goalReachedIcon = itemView.findViewById(R.id.goal_reached);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                goToHistoryContent();
            }

            private void goToHistoryContent() {
                Intent intent = new Intent(History.this.getActivity(), HistoryContent.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", historyContent.get(getAdapterPosition()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

}
