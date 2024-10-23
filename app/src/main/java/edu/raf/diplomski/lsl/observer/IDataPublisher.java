package edu.raf.diplomski.lsl.observer;

import java.util.List;

import edu.raf.diplomski.eeg.Sample;

public interface IDataPublisher {

    void addSubscriber(IDataSubscriber subscriber);
    void removeSubscriber(IDataSubscriber subscriber);
    void notifySubscribers(Sample data);

}
