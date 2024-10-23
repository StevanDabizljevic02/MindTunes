package edu.raf.diplomski.lsl.observer;

import java.util.List;

import edu.raf.diplomski.eeg.Sample;

public interface IDataSubscriber {

    void onData(Sample data);

}
