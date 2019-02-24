package com.example.sip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class History extends Fragment {
    private RecyclerView recyclerView;
    private final LinkedList<String> historyList = new LinkedList<>();
//    String[] strings = {"Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8"};

    public History() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        for (int i = 0; i < 20; i++){
            this.historyList.add("Hello from index " + i);
        }

        View rootView = inflater.inflate(R.layout.fragment_history,null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new RVAdapter(getActivity(), historyContent);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("stepdata");

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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
<<<<<<< HEAD
        recyclerView.setAdapter(new RVAdapter(getActivity(), historyList));
//        return recyclerView;
=======
        recyclerView.setAdapter(adapter);

>>>>>>> 38421495a80bb347e48060ab960482ff497134a0
        return rootView;
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.HistoryViewHolder> {
<<<<<<< HEAD
        private LinkedList<String> dataSource;
        private Context c;
        public RVAdapter(Context c, LinkedList<String> dataArgs) {
=======

        private List<HistoryModel> dataSource;
        private Context c;

        public RVAdapter(Context c, List<HistoryModel> dataArgs) {
>>>>>>> 38421495a80bb347e48060ab960482ff497134a0
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
<<<<<<< HEAD
            historyViewHolder.textView.setText(dataSource.get(position));
=======
            HistoryModel current = dataSource.get(position);
            String stepText = Integer.toString(current.stepCount) + " steps";
            historyViewHolder.dateView.setText(current.date);
            historyViewHolder.stepView.setText(stepText);
            if ((current.target > 0) && (current.stepCount >= current.target)) {
                historyViewHolder.goalReachedIcon.setVisibility(ImageView.VISIBLE);
            } else {
                historyViewHolder.goalReachedIcon.setVisibility(ImageView.GONE);
            }
>>>>>>> 38421495a80bb347e48060ab960482ff497134a0
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
                intent.putExtra("message", dataSource.get(getAdapterPosition()));
                startActivity(intent);
            }
        }
    }


}
