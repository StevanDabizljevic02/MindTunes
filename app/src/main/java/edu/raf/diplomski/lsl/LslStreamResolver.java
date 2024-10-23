package edu.raf.diplomski.lsl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LslStreamResolver {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private LslStreamResolver() {}

    public static CompletableFuture<List<LslStream>> resolveStreamsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            LSL.StreamInfo[] infos = LSL.resolve_streams(2);
            List<LslStream> lslStreams = new ArrayList<>();
            for (LSL.StreamInfo info : infos) {
                lslStreams.add(new LslStream(info));
            }
            return lslStreams;
        }, executor);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
