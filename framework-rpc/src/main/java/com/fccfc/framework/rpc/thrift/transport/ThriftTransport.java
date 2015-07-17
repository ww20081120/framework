package com.fccfc.framework.rpc.thrift.transport;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * ThriftTransport
 * @author yankai
 * @date 2012-8-31
 */
public class ThriftTransport extends TTransport {
    
    /**
     * inputBuffer
     */
    private ChannelBuffer inputBuffer;

    /**
     * outputBuffer
     */
    private ChannelBuffer outputBuffer;

    /**
     * ThriftTransport
     * @param input <br>
     * @param output <br>
     */
    public ThriftTransport(ChannelBuffer input, ChannelBuffer output) {
        this.inputBuffer = input;
        this.outputBuffer = output;
    }

    @Override
    public boolean isOpen() {
        // Buffer is always open
        return true;
    }

    @Override
    public void open() throws TTransportException {
        // Buffer is always open
    }

    @Override
    public void close() {
        // Buffer is always open
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws TTransportException {
        int readableBytes = inputBuffer.readableBytes();
        int bytesToRead = length > readableBytes ? readableBytes : length;

        inputBuffer.readBytes(buffer, offset, bytesToRead);
        return bytesToRead;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws TTransportException {
        outputBuffer.writeBytes(buffer, offset, length);
    }

    public ChannelBuffer getInputBuffer() {
        return inputBuffer;
    }

    public ChannelBuffer getOutputBuffer() {
        return outputBuffer;
    }
}
