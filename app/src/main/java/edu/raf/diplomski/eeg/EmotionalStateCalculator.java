package edu.raf.diplomski.eeg;

import java.util.List;
import java.util.Map;

import edu.raf.diplomski.Constants;

public class EmotionalStateCalculator {

    /**
     * Calculate the emotional state (valence and arousal) based on a Map of Channels and their corresponding Bands.
     *
     * @param channelBandsMap the map of Channels to their corresponding Bands object
     * @param layout the layout type (e.g., 10-20 or smartphones)
     * @return the calculated EmotionalState object
     */
    public EmotionalState calculateEmotionState(Map<Channel, Bands> channelBandsMap, Layout layout) {
        List<String> leftElectrodes;
        List<String> rightElectrodes;

        if(layout == Layout.LAYOUT_10_20){
            leftElectrodes = Constants.LEE_LEFT_HEMISPHERE;
            rightElectrodes = Constants.LEE_RIGHT_HEMISPHERE;
        } else if(layout == Layout.LAYOUT_SMARTPHONES){
            leftElectrodes = Constants.SMARTPHONES_LEFT_HEMISPHERE;
            rightElectrodes = Constants.SMARTPHONES_RIGHT_HEMISPHERE;
        } else {
            throw new IllegalArgumentException("Unsupported layout");
        }

        double valence = calculateValence(channelBandsMap, leftElectrodes, rightElectrodes);
        double arousal = calculateArousal(channelBandsMap, leftElectrodes, rightElectrodes);

        double normalizedValence = ((valence + 1) / 2) * (Constants.VALENCE_UPPER - Constants.VALENCE_LOWER) + Constants.VALENCE_LOWER;
        double normalizedArousal = arousal * (Constants.AROUSAL_UPPER - Constants.AROUSAL_LOWER) + Constants.AROUSAL_LOWER;

        return new EmotionalState(normalizedValence, normalizedArousal);
    }

    /**
     * Calculate the valence based on the alpha power asymmetry of left and right hemisphere electrodes.
     *
     * Valence = (alpha power (Fp1, F3, F7) + alpha power (Fp2, F4, F8)) / (alpha power (Fp1, F3, F7) - alpha power (Fp2, F4, F8))
     *
     * @param channelBandsMap the map of Channels to their corresponding Bands object
     * @param leftElectrodes  the list of left hemisphere electrodes (e.g., Fp1, F3, F7)
     * @param rightElectrodes the list of right hemisphere electrodes (e.g., Fp2, F4, F8)
     * @return the calculated valence value
     */
    private double calculateValence(Map<Channel, Bands> channelBandsMap, List<String> leftElectrodes, List<String> rightElectrodes) {
        double alphaLeft = 0;
        double alphaRight = 0;

        // Sum alpha power for left hemisphere electrodes
        for (String leftElectrode : leftElectrodes) {
            Channel leftChannel = findChannelByName(channelBandsMap, leftElectrode);
            if (leftChannel != null) {
                alphaLeft += channelBandsMap.get(leftChannel).getAlphaWithWholeBand();
            }
        }

        // Sum alpha power for right hemisphere electrodes
        for (String rightElectrode : rightElectrodes) {
            Channel rightChannel = findChannelByName(channelBandsMap, rightElectrode);
            if (rightChannel != null) {
                alphaRight += channelBandsMap.get(rightChannel).getAlphaWithWholeBand();
            }
        }

        // Calculate valence based on alpha power asymmetry
        double numerator = alphaLeft - alphaRight;
        double denominator = alphaLeft + alphaRight;

        // Avoid division by zero
        if (denominator == 0) {
            return 0;  // Neutral valence in case of zero denominator
        }

        System.out.println("Valence: " + numerator / denominator);

        return numerator / denominator;
    }

    /**
     * Calculate the arousal based on the alpha power of the left and right hemisphere electrodes.
     * Arousal = 1 - 2 * (alpha power (Fp1, F3, F7) + alpha power (Fp2, F4, F8))
     *
     * @param leftElectrodes  the list of left hemisphere electrodes (e.g., Fp1, F3, F7)
     * @param rightElectrodes the list of right hemisphere electrodes (e.g., Fp2, F4, F8)
     * @return the calculated arousal value
     */
    public double calculateArousal(Map<Channel, Bands> channelBandsMap, List<String> leftElectrodes, List<String> rightElectrodes) {
        double alphaLeft = 0;
        // Sum alpha power for left hemisphere electrodes
        for (String leftElectrode : leftElectrodes) {
            Channel leftChannel = findChannelByName(channelBandsMap, leftElectrode);
            if (leftChannel != null) {
                alphaLeft += channelBandsMap.get(leftChannel).getAlphaWithWholeBand();
            }else{
                System.out.println("NULL!");
            }
        }

        double alphaRight = 0;
        // Sum alpha power for right hemisphere electrodes
        for (String rightElectrode : rightElectrodes) {
            Channel rightChannel = findChannelByName(channelBandsMap, rightElectrode);
            if (rightChannel != null) {
                alphaRight += channelBandsMap.get(rightChannel).getAlphaWithWholeBand();
            }else{
                System.out.println("NULL!");
            }
        }

        // Apply the formula for arousal
        double averageAlphaPower = (alphaLeft + alphaRight) / 2;
        return 1 - averageAlphaPower;
    }


    /**
     * Helper method to find a Channel object by its name from the map.
     *
     * @param channelBandsMap the map of Channels and their corresponding Bands object
     * @param channelName     the name of the channel to find
     * @return the Channel object, or null if not found
     */
    private Channel findChannelByName(Map<Channel, Bands> channelBandsMap, String channelName) {
        for (Channel channel : channelBandsMap.keySet()) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                return channel;
            }
        }
        throw new RuntimeException("Channel not found: " + channelName);
    }
}
