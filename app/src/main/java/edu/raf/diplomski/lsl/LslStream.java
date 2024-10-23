package edu.raf.diplomski.lsl;

import lombok.Getter;

@Getter
public class LslStream {
    private final LSL.StreamInfo streamInfo;

    public LslStream(LSL.StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public String getName(){
        return streamInfo.name();
    }

    public String getHostName(){
        return streamInfo.hostname();
    }

    public ChannelFormat getChannelFormat(){
        if(streamInfo.channel_format() == LSL.ChannelFormat.float32){
            return ChannelFormat.FLOAT;
        }else if(streamInfo.channel_format() == LSL.ChannelFormat.double64){
            return ChannelFormat.DOUBLE;
        }else{
            return null;
        }
    }



}
