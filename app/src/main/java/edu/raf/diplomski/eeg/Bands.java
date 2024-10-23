package edu.raf.diplomski.eeg;

import lombok.ToString;
import lombok.Value;

@ToString
@Value
public class Bands {
    double alpha;
    double beta;
    double theta;
    double delta;

    double alphaWithWholeBand;
    double betaWithWholeBand;
    double thetaWithWholeBand;
    double deltaWithWholeBand;
}
