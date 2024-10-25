package edu.raf.diplomski;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static List<String> GENRES = Arrays.asList(
            "Pop",
            "Rock",
            "Hip-Hop",
            "Electronic",
            "Jazz",
            "Classical",
            "R&B",
            "Country",
            "Metal",
            "Reggae",
            "Blues",
            "Indie",
            "Folk",
            "Punk",
            "Disco",
            "Soul",
            "Funk",
            "House",
            "Techno",
            "Alternative",
            "Rap",
            "House",
            "Acoustic"
    );

    /**
     * Default big window duration of EEG recording in seconds
     */
    public static final int WINDOW_SIZE_BIG_SECONDS = 30;

    /**
     * Default small window duration of EEG recording in seconds
     */
    public static final int WINDOW_SIZE_SMALL_SECONDS = 5;

    /**
     * Required list of electrodes for both valence and arousal if using Lee's calculation method
     */

    public static final List<String> REQUIRED_ELECTRODES_10_20_LEE = List.of("Fp1", "Fp2", "F3", "F7", "F4", "F8");
    public static final List<String> LEE_LEFT_HEMISPHERE = List.of("Fp1", "F3", "F7");
    public static final List<String> LEE_RIGHT_HEMISPHERE = List.of("Fp2", "F4", "F8");
    /**
     * Required list of electrodes for both valence and arousal if using Reuderink's calculation method
     */
    public static final List<String> REQUIRED_ELECTRODES_10_20_REUDERINK = List.of("Fp1", "Fp2", "Pz", "Cz", "F3", "F4");

    /**
     * Required list of electrodes for both valence and arousal if using Smartphones layout
     */
    public static final List<String> REQUIRED_ELECTRODES_SMARTPHONES = List.of("R1", "R2", "R3", "R4", "C3", "Cz", "C4", "L1", "L2", "L3", "L4");
    public static final List<String> SMARTPHONES_LEFT_HEMISPHERE = List.of("R1", "R2", "R3", "R4", "C3");
    public static final List<String> SMARTPHONES_RIGHT_HEMISPHERE = List.of("C4", "L1", "L2", "L3", "L4");

    public static final double VALENCE_LOWER = 0.235;
    public static final double VALENCE_MEAN = 4.335;
    public static final double VALENCE_UPPER = 8.475;

    public static final double AROUSAL_LOWER = 0.108113;
    public static final double AROUSAL_MEAN = 3.689057;
    public static final double AROUSAL_UPPER = 7.27;
}
