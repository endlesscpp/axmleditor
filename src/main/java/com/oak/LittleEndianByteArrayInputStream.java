package com.oak;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


@Beta
@GwtIncompatible
public final class LittleEndianByteArrayInputStream extends FilterInputStream implements DataInput {
    int mCount; // total count.
    public LittleEndianByteArrayInputStream(ByteArrayInputStream in) {
        super((InputStream) Preconditions.checkNotNull(in));
        mCount = in.available();
    }

    public int size() {
        return mCount;
    }

    public int getCurrentPos() throws IOException {
        return size() - in.available();
    }

    @CanIgnoreReturnValue
    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }

    public void readFully(byte[] b) throws IOException {
        ByteStreams.readFully(this, b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        ByteStreams.readFully(this, b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        return (int)this.in.skip((long)n);
    }

    @CanIgnoreReturnValue
    public int readUnsignedByte() throws IOException {
        int b1 = this.in.read();
        if(0 > b1) {
            throw new EOFException();
        } else {
            return b1;
        }
    }

    @CanIgnoreReturnValue
    public int readUnsignedShort() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        return Ints.fromBytes((byte)0, (byte)0, b2, b1);
    }

    @CanIgnoreReturnValue
    public int readInt() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();
        byte b4 = this.readAndCheckByte();
        return Ints.fromBytes(b4, b3, b2, b1);
    }

    @CanIgnoreReturnValue
    public long readLong() throws IOException {
        byte b1 = this.readAndCheckByte();
        byte b2 = this.readAndCheckByte();
        byte b3 = this.readAndCheckByte();
        byte b4 = this.readAndCheckByte();
        byte b5 = this.readAndCheckByte();
        byte b6 = this.readAndCheckByte();
        byte b7 = this.readAndCheckByte();
        byte b8 = this.readAndCheckByte();
        return Longs.fromBytes(b8, b7, b6, b5, b4, b3, b2, b1);
    }

    @CanIgnoreReturnValue
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @CanIgnoreReturnValue
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @CanIgnoreReturnValue
    public String readUTF() throws IOException {
        return (new DataInputStream(this.in)).readUTF();
    }

    @CanIgnoreReturnValue
    public short readShort() throws IOException {
        return (short)this.readUnsignedShort();
    }

    @CanIgnoreReturnValue
    public char readChar() throws IOException {
        return (char)this.readUnsignedShort();
    }

    @CanIgnoreReturnValue
    public byte readByte() throws IOException {
        return (byte)this.readUnsignedByte();
    }

    @CanIgnoreReturnValue
    public boolean readBoolean() throws IOException {
        return this.readUnsignedByte() != 0;
    }

    private byte readAndCheckByte() throws IOException, EOFException {
        int b1 = this.in.read();
        if(-1 == b1) {
            throw new EOFException();
        } else {
            return (byte)b1;
        }
    }
}
