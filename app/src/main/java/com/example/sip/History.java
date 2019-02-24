package com.example.sip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

//        RecyclerView recyclerView = new RecyclerView(getContext());
//        recyclerView.addItemDecoration(new Divider(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RVAdapter(getActivity(), historyList));
//        return recyclerView;
        return rootView;
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.HistoryViewHolder> {
        private LinkedList<String> dataSource;
        private Context c;
        public RVAdapter(Context c, LinkedList<String> dataArgs) {
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
            historyViewHolder.textView.setText(dataSource.get(position));
        }

        @Override
        public int getItemCount() {
            return dataSource.size();
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView textView;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.word);
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
