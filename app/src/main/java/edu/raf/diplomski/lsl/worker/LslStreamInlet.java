package edu.raf.diplomski.lsl.worker;

import android.util.Log;

import edu.raf.diplomski.eeg.Channel;
import edu.raf.diplomski.eeg.Sample;
import edu.raf.diplomski.lsl.LSL;
import edu.raf.diplomski.lsl.LslHeaderParser;
import edu.raf.diplomski.lsl.LslStream;
import edu.raf.diplomski.lsl.exceptions.CreateInletException;
import edu.raf.diplomski.lsl.observer.IDataPublisher;
import edu.raf.diplomski.lsl.observer.IDataSubscriber;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LslStreamInlet implements IDataPublisher {
    private static final String TAG = LslStreamInlet.class.getSimpleName();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LSL.StreamInfo info;
    private LSL.StreamInlet inlet;
    private List<IDataSubscriber> subscribers;
    private volatile AtomicBoolean running;
    private int numberOfChannels;
    private int channelFormat;
    private List<Channel> channels;

    public LslStreamInlet(LslStream stream) {
        LSL.StreamInfo info = stream.getStreamInfo();
        this.info = info;
        this.subscribers = new CopyOnWriteArrayList<IDataSubscriber>();
        this.running = new AtomicBoolean(false);
        this.numberOfChannels = info.channel_count();
        this.channelFormat = info.channel_format();
        this.channels = LslHeaderParser.parseChannels(stream);

        if(!(info.channel_format() == LSL.ChannelFormat.float32 || info.channel_format() == LSL.ChannelFormat.double64)){
            Log.e(TAG, "Channel format is not float32 or double64");
            throw new CreateInletException("Channel type must be float32 or double64");
        }

        try {
            this.inlet = new LSL.StreamInlet(info, 1);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing stream inlet. Couldn't open inlet.", e);
        }
    }

    public void initialize() {
        running.set(true);
        try {
            inlet.open_stream();
        } catch (Exception e) {
            Log.e(TAG, "Failed to open stream.");
            throw new RuntimeException(e);
        }
        executor.submit(dataCollectorRunnable);
    }

    public void destroy(){
        if(info == null || inlet == null){
            return;
        }
        running.set(false);
        inlet.close();
        executor.shutdown();
        inlet = null;
        info = null;
    }

    public String getInfoAsString(){
        try {
            return inlet.info().as_xml();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't obtain LSL stream info.", e);
        }
    }

    public LSL.XMLElement getDescAsXmlElement(){
        try {
            return inlet.info().desc();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't obtain LSL stream info.", e);
        }
    }

    private final Runnable dataCollectorRunnable = () -> {
        while(running.get()) {
            if (channelFormat == LSL.ChannelFormat.float32) {
                float[] data = new float[numberOfChannels];
                try {
                    double captureTime = this.inlet.pull_sample(data, 0.0);
                    if(captureTime != 0.0)
                        notifySubscribers(transformData(data));
                } catch (Exception e) {
                    Log.e(TAG, "Error while pulling sample", e);
                }
            } else {
                double[] data = new double[numberOfChannels];
                try {
                    double captureTime = this.inlet.pull_sample(data, 0.0);
                    if(captureTime != 0.0)
                        notifySubscribers(transformData(data));
                } catch (Exception e) {
                    Log.e(TAG, "Error while pulling sample", e);
                }
            }
        }
    };

    private Sample transformData(double [] data){
        if (data.length != channels.size()) {
            throw new IllegalArgumentException("The number of data values must match the number of channels.");
        }

        Sample sample = new Sample();

        // Iterate through the channels and add their corresponding data
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            double value = data[i];

            // Add the value to the sample for the corresponding channel
            sample.add(channel, value);
        }

        return sample;
    }

    private Sample transformData(float [] data){
        if (data.length != channels.size()) {
            throw new IllegalArgumentException("The number of data values must match the number of channels.");
        }

        Sample sample = new Sample();

        // Iterate through the channels and add their corresponding data
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            double value = (double) data[i];

            // Add the value to the sample for the corresponding channel
            sample.add(channel, value);
        }

        return sample;
    }

    @Override
    public void addSubscriber(IDataSubscriber subscriber) {
        if(!subscribers.contains(subscriber)){
            subscribers.add(subscriber);
        }
    }

    @Override
    public void removeSubscriber(IDataSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(Sample data) {
        subscribers.forEach(subscriber -> {subscriber.onData(data);});
    }
}
