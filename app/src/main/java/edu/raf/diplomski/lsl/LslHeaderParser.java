package edu.raf.diplomski.lsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.raf.diplomski.eeg.Channel;
import edu.raf.diplomski.lsl.worker.LslStreamInlet;

public class LslHeaderParser {

    public static List<Channel> parseChannels(LslStream stream) {
        LSL.StreamInlet inlet = null;
        LSL.XMLElement descElement = null;
        try {
            inlet = new LSL.StreamInlet(stream.getStreamInfo());
            inlet.open_stream();
            descElement = inlet.info().desc();
//            System.out.println(inlet.info().as_xml());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Initialize the list to hold Channel objects
        List<Channel> channelList = new ArrayList<>();

        // Parse the <channels> element
        LSL.XMLElement channelsElement = descElement.child("channels");
        if (!channelsElement.empty()) {
            // Iterate through each <channel> element inside <channels>
            LSL.XMLElement channelElement = channelsElement.child("channel");
            while (!channelElement.empty()) {
                // Get the channel label (name)
                String label = channelElement.child_value("label");
                // Get the location coordinates (X, Y, Z)
                LSL.XMLElement locationElement = channelElement.child("location");
                float x = Float.parseFloat(locationElement.child_value("X"));
                float y = Float.parseFloat(locationElement.child_value("Y"));
                float z = Float.parseFloat(locationElement.child_value("Z"));

                // Create a Channel object and add it to the list
                Channel channel = new Channel(label, x, y, z);
                channelList.add(channel);

                // Move to the next <channel> element
                channelElement = channelElement.next_sibling("channel");
            }
        }

        inlet.close();
        inlet = null;
        // Return the list of parsed channels
        return channelList;
    }
}
