package edu.raf.diplomski.eeg;

import android.util.Log;

import edu.raf.diplomski.lsl.LslStream;
import edu.raf.diplomski.lsl.observer.IDataSubscriber;
import edu.raf.diplomski.lsl.worker.LslStreamInlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmotionalStateWorker implements IDataSubscriber {

    private static final int DEFAULT_BLOCK_SIZE = 1024;
    private static final int OVERLAP_SIZE = 256;

    private final LslStream lslStream;
    private LslStreamInlet streamInlet;
    private final double sampleRate;
    private final Layout layout;
    private final List<Sample> currentBlock;
    private final List<Map<Channel, Bands>> bands;

    public EmotionalStateWorker(LslStream lslStream, Layout layout) {
        this.lslStream = lslStream;
        this.layout = layout;
        this.sampleRate = lslStream.getStreamInfo().nominal_srate();

        this.currentBlock = new ArrayList<>();
        this.bands = new ArrayList<>();
    }

    /**
     * Starts the LSL stream inlet and begins subscribing to data.
     */
    public void start() {
        streamInlet = new LslStreamInlet(lslStream);
        try {
            streamInlet.initialize();
            streamInlet.addSubscriber(this);
        } catch (Exception e) {
            Log.e("BaselineCalculator", "Error initializing stream inlet", e);
            throw e;
        }
    }

    public Map<Channel, Bands> stopAndGetBandsMean() {
        streamInlet.removeSubscriber(this);
        streamInlet.destroy();

        Map<Channel, double[]> aggregatedBands = new HashMap<>();
        int numberOfWindows = bands.size();

        // Initialize the aggregatedBands map
        for (Map<Channel, Bands> window : bands) {
            for (Map.Entry<Channel, Bands> entry : window.entrySet()) {
                Channel channel = entry.getKey();
                if (!aggregatedBands.containsKey(channel)) {
                    // Initialize arrays for each band (alpha, beta, theta, delta)
                    aggregatedBands.put(channel, new double[8]);
                }
                Bands currentBands = entry.getValue();
                double[] bandSums = aggregatedBands.get(channel);

                // Aggregate the values for alpha, beta, theta, and delta
                bandSums[0] += currentBands.getAlpha();
                bandSums[1] += currentBands.getBeta();
                bandSums[2] += currentBands.getTheta();
                bandSums[3] += currentBands.getDelta();

                // Aggregate the normalized values
                bandSums[4] += currentBands.getAlphaWithWholeBand();
                bandSums[5] += currentBands.getBetaWithWholeBand();
                bandSums[6] += currentBands.getThetaWithWholeBand();
                bandSums[7] += currentBands.getDeltaWithWholeBand();
            }
        }

        // Calculate the mean for each channel and band
        Map<Channel, Bands> meanBandsMap = new HashMap<>();
        for (Map.Entry<Channel, double[]> entry : aggregatedBands.entrySet()) {
            Channel channel = entry.getKey();
            double[] bandSums = entry.getValue();

            // Calculate mean for each band by dividing by the number of windows
            double meanAlpha = bandSums[0] / numberOfWindows;
            double meanBeta = bandSums[1] / numberOfWindows;
            double meanTheta = bandSums[2] / numberOfWindows;
            double meanDelta = bandSums[3] / numberOfWindows;

            double meanAlphaWithWholeBand = bandSums[4] / numberOfWindows;
            double meanBetaWithWholeBand = bandSums[5] / numberOfWindows;
            double meanThetaWithWholeBand = bandSums[6] / numberOfWindows;
            double meanDeltaWithWholeBand = bandSums[7] / numberOfWindows;

            // Create a Bands object for the mean values
            Bands meanBands = new Bands(
                    meanAlpha, meanBeta, meanTheta, meanDelta,
                    meanAlphaWithWholeBand, meanBetaWithWholeBand,
                    meanThetaWithWholeBand, meanDeltaWithWholeBand
            );

            // Store the mean bands for this channel
            meanBandsMap.put(channel, meanBands);
        }

        return meanBandsMap;
    }

    public EmotionalState stopAndGetEmotionalStateMean( ) {
        streamInlet.removeSubscriber(this);
        streamInlet.destroy();

        // List to store the emotional states for each window
        List<EmotionalState> emotionalStates = calculateEmotionalStates();

        // Now, average out the valence and arousal across all windows
        double totalValence = 0;
        double totalArousal = 0;

        for (EmotionalState emotionalState : emotionalStates) {
            totalValence += emotionalState.getValence();
            totalArousal += emotionalState.getArousal();
        }

        double meanValence = totalValence / emotionalStates.size();
        double meanArousal = totalArousal / emotionalStates.size();

        // Return the averaged emotional state
        return new EmotionalState(meanValence, meanArousal);
    }

    public List<EmotionalState> stopAndGetEmotionalStates( ) {
        streamInlet.removeSubscriber(this);
        streamInlet.destroy();
        return calculateEmotionalStates();
    }

    private List<EmotionalState> calculateEmotionalStates(){
        // List to store the emotional states for each window
        List<EmotionalState> emotionalStates = new ArrayList<>();

        // Go through each window's bands and calculate the emotional state
        for (Map<Channel, Bands> window : bands) {
            EmotionalStateCalculator calculator = new EmotionalStateCalculator();
            EmotionalState emotionalStateForWindow = calculator.calculateEmotionState(window, layout);
            emotionalStates.add(emotionalStateForWindow);
        }

        return emotionalStates;
    }

    @Override
    public void onData(Sample sample) {
        // Add data to the current block
        currentBlock.add(sample);

        // If block size reaches the window length, process the block
        if (currentBlock.size() >= DEFAULT_BLOCK_SIZE) {
            processCurrentBlock();

            // Keep the last OVERLAP_SIZE samples for the next window
            List<Sample> overlappingSamples = new ArrayList<>(currentBlock.subList(DEFAULT_BLOCK_SIZE - OVERLAP_SIZE, DEFAULT_BLOCK_SIZE));
            currentBlock.clear();
            currentBlock.addAll(overlappingSamples);
        }
    }

    /**
     * Processes the current block
     */
    private void processCurrentBlock() {
        BandsCalculator bandsCalculator = new BandsCalculator();
        bandsCalculator.calculateBandsAsync(currentBlock, layout, sampleRate).thenAccept(this.bands::add);
    }

}
