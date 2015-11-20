/*
 * =================================================
 * Copyright 2011 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.pcmsampledsp;

import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * TestStereofyAudioInputStream.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestStereofyAudioInputStream {

    @Test
    public void test8BitInput() throws IOException {
        final StereofyAudioInputStream stream = new StereofyAudioInputStream(new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 1, 1, 44100, true),
                8),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 2, 2, 44100, true)
        );
        assertEquals(2, stream.getFormat().getChannels());
        assertEquals(16, stream.available());
        final byte[] buf = new byte[16];
        final int justRead = stream.read(buf);
        assertEquals(16, justRead);
        for (int i=0; i<justRead; i+=2) {
            assertEquals(buf[i], buf[i+1]);
        }
    }

    @Test
    public void test16BitInput() throws IOException {
        final StereofyAudioInputStream stream = new StereofyAudioInputStream(new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, true),
                4),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true)
        );
        assertEquals(2, stream.getFormat().getChannels());
        assertEquals(16, stream.available());
        final byte[] buf = new byte[16];
        final int justRead = stream.read(buf);
        assertEquals(16, justRead);
        for (int i=0; i<justRead; i+=4) {
            assertEquals(buf[i], buf[i+2]);
            assertEquals(buf[i+1], buf[i+3]);
        }
    }

    @Test
    public void test16BitInputMarkRest() throws IOException {
        final StereofyAudioInputStream stream = new StereofyAudioInputStream(new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, true),
                4),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true)
        );
        assertEquals(2, stream.getFormat().getChannels());
        assertEquals(16, stream.available());
        stream.mark(64);
        final byte[] buf = new byte[16];
        stream.read(buf);
        stream.reset();
        final int justRead = stream.read(buf);
        assertEquals(16, justRead);
        for (int i=0; i<justRead; i+=4) {
            assertEquals(buf[i], buf[i+2]);
            assertEquals(buf[i+1], buf[i+3]);
        }
    }
}
