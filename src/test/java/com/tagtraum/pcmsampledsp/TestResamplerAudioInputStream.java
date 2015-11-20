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
 * TestResamplerAudioInputStream.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestResamplerAudioInputStream {

    private static AudioInputStream createShortAudioInputStream() {
        return new AudioInputStream(new ByteArrayInputStream(
                    new byte[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3}
                    ), new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    8,
                    16,
                    2,
                    4,
                    8,
                    true
            ), 8);
    }

    @Test
    public void testStereoUpsample() throws IOException {
        final AudioInputStream stream = createShortAudioInputStream();
        final ResamplerAudioInputStream resampled = new ResamplerAudioInputStream(stream, new Rational(2, 1));

        assertEquals(4*8*2, resampled.available());
        assertEquals(stream.getFrameLength() * 2, resampled.getFrameLength());
        assertEquals(stream.getFormat().getSampleRate() * 2, resampled.getFormat().getSampleRate(), 0.00001f);
        assertEquals(stream.getFormat().getFrameRate() * 2, resampled.getFormat().getFrameRate(), 0.00001f);
        assertEquals(stream.getFormat().getChannels(), resampled.getFormat().getChannels());
        assertEquals(stream.getFormat().getSampleSizeInBits(), resampled.getFormat().getSampleSizeInBits());

        final byte[] buf = new byte[100];
        final int justRead = resampled.read(buf);
        assertEquals(4*8*2, justRead);

        // end of stream
        assertEquals(-1, resampled.read(buf));
    }


}
