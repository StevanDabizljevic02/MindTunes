package edu.raf.diplomski.eeg;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
@Value
public class Channel {
    String name;
    float x;
    float y;
    float z;
}
