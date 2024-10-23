
# MindTunes

## Overview

MindTunes is an Android application that uses real-time EEG data analysis to recommend music based on the user's current emotional state. It leverages the emotional metrics of valence and arousal, derived from EEG signals, to sync music with the user's mood, enhancing their listening experience.

## Features

- Real-time EEG signal processing for emotional state detection.
- Music recommendation based on calculated valence and arousal.
- Integration with Spotify for a seamless music streaming experience.
- Compatibility with various EEG systems using the Lab Streaming Layer (LSL) protocol.

## Installation

Clone the repository to your local machine using:

```bash
git clone https://github.com/yourusername/mindtunes.git
```

## Prerequisites

- Android Studio
- JDK 8 or newer
- An active Spotify Premium account
- A compatible EEG device with LSL support

## Building the Application

1. **Open the Project:**
   - Launch Android Studio.
   - Open the `MindTunes` project by selecting 'Open an Existing Project' and navigating to the project directory.

2. **Configure the Environment:**
   - Ensure that the `local.properties` file in the project root includes the path to your Android SDK. E.g.,
     ```
     sdk.dir=/path/to/android/sdk
     ```

3. **Build the Project:**
   - In Android Studio, select 'Build' from the top menu.
   - Click on 'Make Project' to compile the application.

4. **Run the Application:**
   - Connect a compatible Android device via USB or set up an Android emulator.
   - Press 'Run' in Android Studio and choose the connected device or emulator.

5. **Set up EEG Device:**
   - Ensure your EEG device is configured and transmitting data over LSL.

## Usage

1. Start MindTunes and log in to your Spotify Premium account.
2. Connect to your EEG device via the app's interface.
3. Allow the app to access EEG data and start receiving music recommendations based on your emotional state.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

- Thanks to mBrainTrain.com for providing the EEG systems used during development and testing.
