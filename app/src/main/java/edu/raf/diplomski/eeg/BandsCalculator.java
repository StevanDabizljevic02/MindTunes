package edu.raf.diplomski.eeg;

import com.github.psambit9791.jdsp.transform.DiscreteFourier;
import com.github.psambit9791.jdsp.transform.FastFourier;

import org.apache.commons.math3.transform.FastFourierTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import edu.raf.diplomski.Constants;

public class BandsCalculator {

    // Frequency bands for EEG (in Hz)
    private static final double ALPHA_LOW = 8.0;
    private static final double ALPHA_HIGH = 12.0;
    private static final double BETA_LOW = 12.0;
    private static final double BETA_HIGH = 30.0;
    private static final double THETA_LOW = 4.0;
    private static final double THETA_HIGH = 8.0;
    private static final double DELTA_LOW = 0.5;
    private static final double DELTA_HIGH = 4.0;

    // Frequency step (calculated based on sampling rate and signal length)
    private double frequencyStep;

    /**
     * This method calculates the EEG frequency bands for each channel.
     * Each band is normalized by the total power of all bands for that specific channel.
     * @param eegData The EEG data samples.
     * @param layout The layout type (e.g., 10-20 or smartphones).
     * @param samplingRate The sampling rate of the EEG data.
     * @return A CompletableFuture that contains a Map of Channels and their corresponding Bands.
     */
    public CompletableFuture<Map<Channel, Bands>> calculateBandsAsync(List<Sample> eegData, Layout layout, double samplingRate) {
        // Determine the channel labels based on the layout
        List<String> channelLabels;
        if (layout == Layout.LAYOUT_10_20) {
            channelLabels = Constants.REQUIRED_ELECTRODES_10_20_LEE;
        } else if (layout == Layout.LAYOUT_SMARTPHONES) {
            channelLabels = Constants.REQUIRED_ELECTRODES_SMARTPHONES;
        } else {
            throw new IllegalArgumentException("Unsupported layout");
        }

        // Create a map to hold the double arrays for each channel
        Map<String, List<Double>> channelData = new HashMap<>();

        // Initialize a list for each channel
        for (String channel : channelLabels) {
            channelData.put(channel, new ArrayList<>());
        }

        // Repack data: extract relevant values from the samples
        for (Sample sample : eegData) {
            for (String channelLabel : channelLabels) {
                Double value = sample.get(channelLabel);
                if (value != null) {
                    channelData.get(channelLabel).add(value);
                } else {
                    throw new IllegalArgumentException("Missing data for channel: " + channelLabel);
                }
            }
        }

        // Convert List<Double> to double[]
        Map<String, double[]> channelDataArray = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : channelData.entrySet()) {
            double[] dataArray = entry.getValue().stream().mapToDouble(Double::doubleValue).toArray();
            channelDataArray.put(entry.getKey(), dataArray);
        }

        // Set the frequency step for FFT based on sampling rate and signal length
        frequencyStep = samplingRate / channelDataArray.values().iterator().next().length;

        // Create a map to hold Bands for each channel
        Map<Channel, Bands> channelBandsMap = new HashMap<>();

        return CompletableFuture.supplyAsync(() -> {
            // Calculate bands for each channel asynchronously using FFT
            for (Map.Entry<String, double[]> entry : channelDataArray.entrySet()) {
                String channelLabel = entry.getKey();
                double[] channelSignal = entry.getValue();

                // Compute FFT
                FastFourier fft = new FastFourier(channelSignal);
                fft.transform();
                double[] magnitudes = fft.getMagnitude(false);

                // Calculate the power in each band for this channel
                double alpha = calculatePowerInBand(magnitudes, ALPHA_LOW, ALPHA_HIGH);
                double beta = calculatePowerInBand(magnitudes, BETA_LOW, BETA_HIGH);
                double theta = calculatePowerInBand(magnitudes, THETA_LOW, THETA_HIGH);
                double delta = calculatePowerInBand(magnitudes, DELTA_LOW, DELTA_HIGH);

                // Calculate total power for normalization for this channel
                double totalPower = alpha + beta + theta + delta;

                // Normalize each band by the total power of this channel
                double alphaWithWholeBand = alpha / totalPower;
                double betaWithWholeBand = beta / totalPower;
                double thetaWithWholeBand = theta / totalPower;
                double deltaWithWholeBand = delta / totalPower;

                // Create a Bands object for this channel and store it in the map
                Channel channel = new Channel(channelLabel, 0, 0, 0);  // Update with correct Channel object
                Bands bands = new Bands(alpha, beta, theta, delta, alphaWithWholeBand, betaWithWholeBand, thetaWithWholeBand, deltaWithWholeBand);
                channelBandsMap.put(channel, bands);
            }

            // Return the map of Channel to Bands
            return channelBandsMap;
        });
    }

    // Method to calculate the power in a specific frequency band
    private double calculatePowerInBand(double[] magnitudes, double lowFreq, double highFreq) {
        int lowIndex = (int) Math.floor(lowFreq / frequencyStep);
        int highIndex = (int) Math.ceil(highFreq / frequencyStep);
        double bandPower = 0;
        for (int i = lowIndex; i <= highIndex; i++) {
            // Square magnitude to get power
            bandPower += magnitudes[i] * magnitudes[i];
        }
        return bandPower;
    }

}
