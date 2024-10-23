package edu.raf.diplomski.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import edu.raf.diplomski.R;
import edu.raf.diplomski.lsl.LSL;
import edu.raf.diplomski.lsl.LslHeaderParser;
import edu.raf.diplomski.lsl.LslStream;
import edu.raf.diplomski.lsl.worker.LslStreamInlet;

public class LslStreamAdapter extends RecyclerView.Adapter<LslStreamAdapter.StreamViewHolder> {

    private List<LslStream> streamList;
    private int selectedPosition = -1; // Track the selected position

    public LslStreamAdapter(List<LslStream> streamList) {
        this.streamList = streamList;
    }

    @NonNull
    @Override
    public StreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lsl_stream, parent, false);
        return new StreamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamViewHolder holder, int position) {
        LslStream stream = streamList.get(position);
        holder.streamName.setText(stream.getName()); // Set the stream name

        holder.radioButton.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return streamList.size();
    }

    public LslStream getSelectedStream() {
        if (selectedPosition != -1) {
            return streamList.get(selectedPosition);
        }
        return null;
    }

    public void setStreams(List<LslStream> streams) {
        this.streamList = streams;
        notifyDataSetChanged();
    }

    public static class StreamViewHolder extends RecyclerView.ViewHolder {

        public TextView streamName;
        public RadioButton radioButton;

        public StreamViewHolder(@NonNull View itemView) {
            super(itemView);
            streamName = itemView.findViewById(R.id.stream_name);
            radioButton = itemView.findViewById(R.id.stream_radio_button);

            radioButton.setButtonTintList(itemView.getContext().getResources().getColorStateList(R.color.white));
        }
    }
}
