package edu.raf.diplomski.gui.regular;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.raf.diplomski.Constants;
import edu.raf.diplomski.R;
import edu.raf.diplomski.eeg.Channel;
import edu.raf.diplomski.eeg.Layout;
import edu.raf.diplomski.gui.AppViewModel;
import edu.raf.diplomski.gui.LslStreamAdapter;
import edu.raf.diplomski.lsl.ChannelFormat;
import edu.raf.diplomski.lsl.LslHeaderParser;
import edu.raf.diplomski.lsl.LslStream;
import edu.raf.diplomski.lsl.LslStreamResolver;
import edu.raf.diplomski.lsl.worker.LslStreamInlet;

public class LslStreamsFragment extends Fragment {

    private AppViewModel viewModel;
    private LslStreamAdapter streamAdapter;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lsl_streams, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        progressBar = view.findViewById(R.id.stream_resolving_spinner);

        RecyclerView recyclerView = view.findViewById(R.id.streams_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        streamAdapter = new LslStreamAdapter(List.of());
        recyclerView.setAdapter(streamAdapter);

        Button resolveButton = view.findViewById(R.id.resolve_streams_button);
        resolveButton.setOnClickListener(v -> resolveStreams());

        Button nextButton = view.findViewById(R.id.to_calibration_button);
        nextButton.setOnClickListener(v -> {
            LslStream selectedStream = streamAdapter.getSelectedStream();
            if (selectedStream == null) {
                Toast.makeText(getContext(), "Please select a stream", Toast.LENGTH_SHORT).show();
            } else {
                if(checkSelectedStream()) {

                    viewModel.setSelectedStream(selectedStream);
                    viewModel.setSelectedStreamChannels(LslHeaderParser.parseChannels(selectedStream));

                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_lslSelection_to_calibration);
                }
            }
        });

        // Resolve streams to show them initially
        resolveStreams();

        return view;
    }

    private boolean checkSelectedStream(){
        LslStream stream = streamAdapter.getSelectedStream();

        if(!(stream.getChannelFormat() == ChannelFormat.FLOAT || stream.getChannelFormat() == ChannelFormat.DOUBLE)){
            Toast.makeText(this.getContext(), "Selected LSL is not Double or Float formated.", Toast.LENGTH_SHORT).show();
            return false;
        }

        List<Channel> channels = LslHeaderParser.parseChannels(stream);

        if(!stream.getStreamInfo().type().equalsIgnoreCase("eeg")){
            Toast.makeText(this.getContext(), "Selected LSL is not EEG.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(channels.isEmpty()){
            Toast.makeText(this.getContext(), "Couldn't parse channel locations.", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean leeLayout = checkChannelsForElectrodes(channels, Constants.REQUIRED_ELECTRODES_10_20_LEE);
        boolean smartphonesLayout = checkChannelsForElectrodes(channels, Constants.REQUIRED_ELECTRODES_SMARTPHONES);

        if(leeLayout){
            Toast.makeText(this.getContext(), "10-20 layout detected. Selecting channels...", Toast.LENGTH_SHORT).show();
            viewModel.setLayout(Layout.LAYOUT_10_20);
        }else if (smartphonesLayout){
            Toast.makeText(this.getContext(), "Smartphones layout detected. Selecting channels...", Toast.LENGTH_SHORT).show();
            viewModel.setLayout(Layout.LAYOUT_SMARTPHONES);
        }else{
            Toast.makeText(this.getContext(), "One or more required electrodes are missing.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkChannelsForElectrodes(List<Channel> channels, List<String> requiredElectrodes){
        for (String requiredElectrode : requiredElectrodes) {
            boolean found = false;
            for (Channel channel : channels) {
                if (channel.getName().equalsIgnoreCase(requiredElectrode)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private void resolveStreams() {
        progressBar.setVisibility(View.VISIBLE);

        streamAdapter.setStreams(List.of());

        LslStreamResolver.resolveStreamsAsync().thenAccept(streams -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (streams.isEmpty()) {
                    Toast.makeText(getContext(), "No streams found", Toast.LENGTH_SHORT).show();
                } else {
                    streamAdapter.setStreams(streams);
                }
            });
        }).exceptionally(ex -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to resolve streams", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }
}
