/*
 * =================================================
 * Copyright 2011 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.pcmsampledsp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * TestBytesIntConverter.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestBytesIntConverter {

    @Test
    public void testByteToInt() throws IOException {
        final byte[] bytes = {-128, -128, 3, 4};
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.rewind();
        assertEquals(-128, BytesIntConverter.getInstance(1, true, true).decode(byteBuffer));
    }

    @Test
    public void test24BitSignedPositive() throws IOException {
        final byte[] bytes = {64, 63, 3};
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        assertEquals(4210435, BytesIntConverter.getInstance(3, true, true).decode(byteBuffer));
    }

    @Test
    public void test24BitBigEndianToInt() throws IOException {
        final byte[] bytes = {-128, -128, 3, 4};
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.rewind();
        assertEquals(-8355837, BytesIntConverter.getInstance(3, true, true).decode(byteBuffer));

        byteBuffer.rewind();
        assertEquals(8421379, BytesIntConverter.getInstance(3, true, false).decode(byteBuffer));
    }

    @Test
    public void test16BitToInt() throws IOException {
        final byte[] bytes = {-128, -128, 3, 4};
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.rewind();
        // signed short is always Big endian
        final int signedShort = new DataInputStream(new ByteArrayInputStream(bytes)).readShort();
        assertEquals(signedShort, BytesIntConverter.getInstance(2, true, true).decode(byteBuffer));

        byteBuffer.rewind();
        final ByteArrayInputStream littleEndian = new ByteArrayInputStream(bytes);
        int ch1 = littleEndian.read();
        int ch2 = littleEndian.read();
        final int signedShortLittleEndian = (short) ((ch2 << 8) + (ch1 << 0));
        assertEquals(signedShortLittleEndian, BytesIntConverter.getInstance(2, false, true).decode(byteBuffer));


        byteBuffer.rewind();
        // signed short is always Big endian
        final int unsignedShort = new DataInputStream(new ByteArrayInputStream(bytes)).readUnsignedShort();
        assertEquals(unsignedShort, BytesIntConverter.getInstance(2, true, false).decode(byteBuffer));
    }

    @Test
    public void testIntToByte() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        final int value = -128;
        BytesIntConverter.getInstance(1, true, true).encode(value, byteBuffer);

        byteBuffer.rewind();
        assertEquals(-128, byteBuffer.get());
    }

    @Test
    public void testIntTo24Bit() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        BytesIntConverter.getInstance(3, true, true).encode(4210435, byteBuffer);

        byteBuffer.rewind();
        assertEquals(64, byteBuffer.get());
        assertEquals(63, byteBuffer.get());
        assertEquals(3, byteBuffer.get());
    }

    @Test
    public void testIntTo16Bit() {
        /*
        final byte[] bytes = {64, -128};
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final int be = ResamplerAudioInputStream.byteToInt(bb, 2, true, true);
        bb.rewind();
        final int le = ResamplerAudioInputStream.byteToInt(bb, 2, false, true);
        */

        final ByteBuffer byteBuffer = ByteBuffer.allocate(4);

        byteBuffer.rewind();
        BytesIntConverter.getInstance(2, true, true).encode(16512, byteBuffer);
        byteBuffer.rewind();
        assertEquals(64, byteBuffer.get());
        assertEquals(-128, byteBuffer.get());


        byteBuffer.rewind();
        final int value = -32704;
        BytesIntConverter.getInstance(2, false, true).encode(value, byteBuffer);
        byteBuffer.rewind();
        assertEquals(64, byteBuffer.get());
        assertEquals(-128, byteBuffer.get());
    }

}
