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
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * TestStereofyConversionProvider.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestStereofyConversionProvider {

    @Test
    public void testGetAudioInputStream() {
        final AudioFormat monoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, true);
        final AudioFormat stereoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
        final AudioInputStream monoStream = new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}),
                monoFormat,
                4);


        final AudioInputStream stream = new StereofyConversionProvider().getAudioInputStream(stereoFormat, monoStream);
        assertNotNull(stream);
    }

    @Test
    public void testGetAudioInputStreamFive() {
        final AudioInputStream fiveChannelStream = new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 5, 10, 44100, true),
                1);
        final AudioFormat stereoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);

        try {
            new StereofyConversionProvider().getAudioInputStream(stereoFormat, fiveChannelStream);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected this
        }
    }

    @Test
    public void testSPI() {
        final AudioFormat monoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, true);
        final AudioFormat stereoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
        final boolean conversionSupported = AudioSystem.isConversionSupported(stereoFormat, monoFormat);
        assertTrue(conversionSupported);

        final AudioInputStream monoStream = new AudioInputStream(
                new ByteArrayInputStream(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}),
                monoFormat,
                4);
        final AudioInputStream stereoStream = AudioSystem.getAudioInputStream(stereoFormat, monoStream);
        assertNotNull(stereoStream);
    }
}
