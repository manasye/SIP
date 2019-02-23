package com.example.sip;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class History extends Fragment {
    private RecyclerView recyclerView;
    String[] strings = {"Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8"};
    public History() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history,null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

//        RecyclerView recyclerView = new RecyclerView(getContext());
//        recyclerView.addItemDecoration(new Divider(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RVAdapter(getActivity(), strings));
//        return recyclerView;
        return rootView;
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.HistoryViewHolder> {
        private String[] dataSource;
        private Context c;
        public RVAdapter(Context c, String[] dataArgs) {
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
            historyViewHolder.textView.setText(dataSource[position]);
        }

        @Override
        public int getItemCount() {
            return dataSource.length;
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.word);
            }
        }
    }
}
