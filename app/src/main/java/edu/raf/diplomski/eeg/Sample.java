package edu.raf.diplomski.eeg;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


public class Sample {
    private final Map<Channel, Double> values = new HashMap<>();

    /**
     * Adds or updates the value for the given Channel (float input, converted to double).
     *
     * @param channel the Channel object to set the value for.
     * @param value the float value to be associated with the Channel, will be converted to double.
     */
    public void add(Channel channel, float value) {
        values.put(channel, (double) value);
    }

    /**
     * Adds or updates the value for the given Channel (double input).
     *
     * @param channel the Channel object to set the value for.
     * @param value the double value to be associated with the Channel.
     */
    public void add(Channel channel, double value) {
        values.put(channel, value);
    }

    /**
     * Retrieves the value for the given Channel.
     *
     * @param channel the Channel object to get the value for.
     * @return the double value associated with the given Channel, or null if the channel does not exist.
     */
    public Double get(Channel channel) {
        return values.get(channel);
    }

    /**
     * Adds or updates the value for the Channel whose name matches the given String (float input, converted to double).
     *
     * @param channelName the name of the Channel to set the value for.
     * @param value the float value to be associated with the Channel, will be converted to double.
     */
    public void add(String channelName, float value) {
        Channel matchingChannel = findChannelByName(channelName);
        if (matchingChannel != null) {
            values.put(matchingChannel, (double) value);
        } else {
            throw new IllegalArgumentException("Channel with name '" + channelName + "' not found.");
        }
    }

    /**
     * Adds or updates the value for the Channel whose name matches the given String (double input).
     *
     * @param channelName the name of the Channel to set the value for.
     * @param value the double value to be associated with the Channel.
     */
    public void add(String channelName, double value) {
        Channel matchingChannel = findChannelByName(channelName);
        if (matchingChannel != null) {
            values.put(matchingChannel, value);
        } else {
            throw new IllegalArgumentException("Channel with name '" + channelName + "' not found.");
        }
    }

    /**
     * Retrieves the value for the Channel whose name matches the given String.
     *
     * @param channelName the name of the Channel to get the value for.
     * @return the double value associated with the Channel, or null if the channel does not exist.
     */
    public Double get(String channelName) {
        Channel matchingChannel = findChannelByName(channelName);
        if (matchingChannel != null) {
            return values.get(matchingChannel);
        } else {
            throw new IllegalArgumentException("Channel with name '" + channelName + "' not found.");
        }
    }

    /**
     * Finds a Channel object by its name.
     *
     * @param channelName the name of the Channel to find.
     * @return the Channel object with the matching name, or null if no such channel is found.
     */
    private Channel findChannelByName(String channelName) {
        for (Channel channel : values.keySet()) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                return channel;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Channel, Double> entry : values.entrySet()) {
            sb.append(entry.getKey().getName()).append(":").append(entry.getValue()).append(", ");
        }
        // Remove the trailing comma and space
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
