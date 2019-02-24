package com.example.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class History extends Fragment {
    public static final String HISTORY_REFRESH_EVENT = "history-refresh";
    private RecyclerView recyclerView;
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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("stepdata");
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

                    historyContent.add(new HistoryModel(date,stepCount,target));
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
        View rootView = inflater.inflate(R.layout.fragment_history,null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new RVAdapter(getActivity(), historyContent);

        updateHistoryList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(msgReceiver,
                new IntentFilter(HISTORY_REFRESH_EVENT));

        return rootView;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(msgReceiver);
        super.onPause();
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.HistoryViewHolder> {

        private List<HistoryModel> dataSource;
        private Context c;

        public RVAdapter(Context c, List<HistoryModel> dataArgs) {
            this.c = c;
            this.dataSource = dataArgs;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(c).inflate(R.layout.history_items, parent, false);
            HistoryViewHolder viewHolder = new HistoryViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int position) {
            HistoryModel current = dataSource.get(position);
            String stepText = Integer.toString(current.stepCount) + " steps";
            historyViewHolder.dateView.setText(current.date);
            historyViewHolder.stepView.setText(stepText);
            if ((current.target > 0) && (current.stepCount >= current.target)) {
                historyViewHolder.goalReachedIcon.setVisibility(ImageView.VISIBLE);
            } else {
                historyViewHolder.goalReachedIcon.setVisibility(ImageView.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return dataSource.size();
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView dateView;
            public TextView stepView;
            public ImageView goalReachedIcon;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                dateView = (TextView) itemView.findViewById(R.id.date);
                stepView = (TextView) itemView.findViewById(R.id.steps);
                goalReachedIcon = (ImageView) itemView.findViewById(R.id.goal_reached);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                goToHistoryContent();
            }

            private void goToHistoryContent() {
                Intent intent = new Intent(History.this.getActivity(), HistoryContent.class);
                startActivity(intent);
            }
        }
    }

}
