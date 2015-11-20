/*
 * =================================================
 * Copyright 2011 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.pcmsampledsp;

import org.junit.Test;

import javax.sound.sampled.*;
import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * TestSampleSizeConversionProvider.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestSampleSizeConversionProvider {

    @Test
    public void testDecreaseBitDepthAudioFile() throws IOException, UnsupportedAudioFileException {

        final String filename = "test.wav";
        final File file = File.createTempFile("resampleWaveFile", filename);
        final File out = File.createTempFile("resampledWaveFile", filename);
        extractFile(filename, file);

        final long start = System.currentTimeMillis();

        final AudioInputStream sourceStream = AudioSystem.getAudioInputStream(file);
        final AudioFormat sourceFormat = sourceStream.getFormat();
        final AudioFormat targetFormat = new AudioFormat(
                sourceFormat.getEncoding(),
                sourceFormat.getSampleRate(),
                8,
                sourceFormat.getChannels(),
                2,
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian()
        );
        assertTrue(AudioSystem.isConversionSupported(targetFormat, sourceFormat));

        System.out.println(out);
        final AudioInputStream resampledStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
        AudioSystem.write(resampledStream, AudioFileFormat.Type.WAVE, out);
        file.delete();
        // this allows us to actually listen to the thing... but for now we just delete it
        out.delete();

        System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testIncreaseBitDepthAudioFile() throws IOException, UnsupportedAudioFileException {

        final String filename = "test.wav";
        final File file = File.createTempFile("resampleWaveFile", filename);
        final File out = File.createTempFile("resampledWaveFile", filename);
        extractFile(filename, file);

        final long start = System.currentTimeMillis();

        final AudioInputStream sourceStream = AudioSystem.getAudioInputStream(file);
        final AudioFormat sourceFormat = sourceStream.getFormat();
        final AudioFormat targetFormat = new AudioFormat(
                sourceFormat.getEncoding(),
                sourceFormat.getSampleRate(),
                24,
                sourceFormat.getChannels(),
                6,
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian()
        );
        assertTrue(AudioSystem.isConversionSupported(targetFormat, sourceFormat));

        System.out.println(out);
        final AudioInputStream resampledStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
        AudioSystem.write(resampledStream, AudioFileFormat.Type.WAVE, out);
        file.delete();
        // this allows us to actually listen to the thing... but for now we just delete it
        out.delete();

        System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    private void extractFile(final String filename, final File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getResourceAsStream(filename);
            out = new FileOutputStream(file);
            final byte[] buf = new byte[1024*64];
            int justRead;
            while ((justRead = in.read(buf)) != -1) {
                out.write(buf, 0, justRead);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
