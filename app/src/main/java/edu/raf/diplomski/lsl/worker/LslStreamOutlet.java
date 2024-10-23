package edu.raf.diplomski.lsl.worker;

import android.util.Log;

import edu.raf.diplomski.eeg.Sample;
import edu.raf.diplomski.lsl.ChannelFormat;
import edu.raf.diplomski.lsl.LSL;
import edu.raf.diplomski.lsl.observer.IDataSubscriber;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

@Getter
public class LslStreamOutlet implements IDataSubscriber {

    private static final String TAG = LslStreamOutlet.class.getSimpleName();
    private LSL.StreamInfo info;
    private LSL.StreamOutlet outlet;
    private String name;
    private String type;
    private int channelCount;
    private double nominalSRate;
    private ChannelFormat channelFormat;

    public LslStreamOutlet(String name, String type, int channelCount, double nominalSRate, ChannelFormat channelFormat) {
        this.name = name;
        this.type = type;
        this.channelCount = channelCount;
        this.nominalSRate = nominalSRate;
        this.channelFormat = channelFormat;
        this.info = new LSL.StreamInfo(name, type, channelCount, nominalSRate, getChannelFormat(channelFormat));
    }

    public void initialize(){
        try {
            this.outlet = new LSL.StreamOutlet(info);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing outlet", e);
        }
    }

    public void destroy(){

    }

    @Override
    public void onData(Sample data) {
        // TODO IDK IF I NEED YOU, IF I DO, THEN I WILL FIX YOU
//        if(data.size() != channelCount){
//            Log.e(TAG, "Data size does not match channel count");
//            throw new RuntimeException("Data size does not match channel count");
//        }
//        switch (channelFormat) {
//            case FLOAT -> {
//                outlet.push_sample(repackDataFloat(data));
//            }
//            case DOUBLE -> {
//                outlet.push_sample(repackDataDouble(data));
//            }
//            default -> Log.e(TAG, "Unknown channel format");
//        }
    }

    double [] repackDataDouble(List<Double> data){
        double [] dataFloat = new double[data.size()];
        for(int i=0; i<data.size(); i++){
            dataFloat[i] = data.get(i);
        }
        return dataFloat;
    }

    float [] repackDataFloat(List<Double> data){
        float [] dataFloat = new float[data.size()];
        for(int i=0; i<data.size(); i++){
            dataFloat[i] = data.get(i).floatValue();
        }
        return dataFloat;
    }

    private int getChannelFormat(ChannelFormat channelFormat) {
        switch (channelFormat) {
            case FLOAT -> {
                return LSL.ChannelFormat.float32;
            }
            case DOUBLE -> {
                return LSL.ChannelFormat.double64;
            }
        }
        throw new RuntimeException("Incompatible type exception");
    }
}
