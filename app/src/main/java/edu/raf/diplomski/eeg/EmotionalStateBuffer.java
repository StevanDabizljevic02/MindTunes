package edu.raf.diplomski.eeg;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class EmotionalStateBuffer {

    private final List<Double> arousalBuffer;
    private final List<Double> valenceBuffer;
    @Getter
    private final int bufferSize;

    public EmotionalStateBuffer(int bufferSize) {
        arousalBuffer = new ArrayList<>();
        valenceBuffer = new ArrayList<>();
        this.bufferSize = bufferSize;
    }

    public void putValue(EmotionalState emotionalState) {
        if(arousalBuffer.size() == bufferSize) {
            arousalBuffer.remove(0);
            valenceBuffer.remove(0);
        }
        arousalBuffer.add(emotionalState.getArousal());
        valenceBuffer.add(emotionalState.getValence());
    }

    public double getArousalMean() {
        return arousalBuffer.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public double getValenceMean() {
        return valenceBuffer.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public void clear() {
        arousalBuffer.clear();
        valenceBuffer.clear();
    }

}
