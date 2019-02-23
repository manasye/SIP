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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link History.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {
    String[] strings = {"Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8", "Hello1", "Hello2", "Hello3", "Hello4", "Hello5", "Hello6", "Hello7", "Hello8"};
    public History() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RVAdapter(strings));
        return recyclerView;
    }

    public class RVAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        private String[] dataSource;
        public RVAdapter(String[] dataArgs) {
            this.dataSource = dataArgs;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = new TextView(parent.getContext());
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
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
