/*
 * =================================================
 * Copyright 2011 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.pcmsampledsp;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * TestResampler.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestResampler {

    @Test
    public void testStereoUpsampling() {
        final int[] stereo = {0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000};

        final int upFactor = 3;
        final int downFactor = 1;
        final int[] stereoResult = new int[stereo.length * upFactor];
        // resample interleaved channel 0
        final Resampler stereoResampler0 = new Resampler(new Rational(upFactor, downFactor));
        stereoResampler0.resample(stereo, stereoResult, 0, 2);
        // verify that channel 1 stayed untouched
        for (int i=0; i<stereo.length; i+=2) {
            assertEquals(stereoResult[i+1], 0);
        }
        final int[] stereoResultClone = stereoResult.clone();
        // resample interleaved channel 1
        final Resampler stereoResampler1 = new Resampler(new Rational(upFactor, downFactor));
        stereoResampler1.resample(stereo, stereoResult, 1, 2);
        // verify that channel 0 of the already computed result stayed untouched
        for (int i=0; i<stereoResult.length; i+=2) {
            assertEquals(stereoResultClone[i], stereoResult[i]);
        }

        // now compare with single channel versions
        final int[] channel0 = new int[stereo.length/2];
        final int[] channel1 = new int[stereo.length/2];
        for (int i=0; i<stereo.length; i+=2) {
            channel0[i/2] = stereo[i];
            channel1[i/2] = stereo[i+1];
        }
        final int[] channel0Result = new int[channel0.length*upFactor];
        final int[] channel1Result = new int[channel1.length*upFactor];

        final Resampler monoResampler0 = new Resampler(new Rational(upFactor, downFactor));
        final Resampler monoResampler1 = new Resampler(new Rational(upFactor, downFactor));
        monoResampler0.resample(channel0, channel0Result, 0, 1);
        monoResampler1.resample(channel1, channel1Result, 0, 1);

        for (int i=0; i<stereoResult.length; i+=2) {
            assertEquals(stereoResult[i], channel0Result[i/2]);
            assertEquals(stereoResult[i+1], channel1Result[i/2]);
        }

        // now compare with manually upsampled version
        final float[] manualChannel0In = new float[channel0Result.length];
        // first add zeroes by stretching
        for (int i=0; i<manualChannel0In.length; i+=upFactor) {
            manualChannel0In[i] = channel0[i/upFactor];
        }
        // then apply filter
        final FIRFilter lowpass = FIRFilter.createFir1_29thOrderLowpass(upFactor);
        final float[] manualChannel0 = lowpass.filter(manualChannel0In);
        // result should be the same as channel0Result
        for (int i=0; i<manualChannel0.length; i++) {
            // we need to upscale the amplitude for equal loudness
            assertEquals(Math.round(manualChannel0[i] * upFactor), channel0Result[i]);
        }
    }

    @Test
    public void testStereoDownsampling() {
        final int[] stereo = {0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000};

        final int upFactor = 1;
        final int downFactor = 3;
        final int[] stereoResult = new int[stereo.length / downFactor];
        // resample interleaved channel 0
        final Resampler stereoResampler0 = new Resampler(new Rational(upFactor, downFactor));
        stereoResampler0.resample(stereo, stereoResult, 0, 2);
        // verify that channel 1 stayed untouched
        for (int i=0; i<stereoResult.length; i+=2) {
            assertEquals(stereoResult[i+1], 0);
        }
        final int[] stereoResultClone = stereoResult.clone();
        // resample interleaved channel 1
        final Resampler stereoResampler1 = new Resampler(new Rational(upFactor, downFactor));
        stereoResampler1.resample(stereo, stereoResult, 1, 2);
        // verify that channel 0 of the already computed result stayed untouched
        for (int i=0; i<stereoResult.length; i+=2) {
            assertEquals(stereoResultClone[i], stereoResult[i]);
        }

        // now compare with single channel versions
        final int[] channel0 = new int[stereo.length/2];
        final int[] channel1 = new int[stereo.length/2];
        for (int i=0; i<stereo.length; i+=2) {
            channel0[i/2] = stereo[i];
            channel1[i/2] = stereo[i+1];
        }
        final int[] channel0Result = new int[channel0.length/downFactor];
        final int[] channel1Result = new int[channel1.length/downFactor];

        final Resampler monoResampler0 = new Resampler(new Rational(upFactor, downFactor));
        final Resampler monoResampler1 = new Resampler(new Rational(upFactor, downFactor));
        monoResampler0.resample(channel0, channel0Result, 0, 1);
        monoResampler1.resample(channel1, channel1Result, 0, 1);

        for (int i=0; i<stereoResult.length; i+=2) {
            assertEquals(stereoResult[i], channel0Result[i/2]);
            assertEquals(stereoResult[i+1], channel1Result[i/2]);
        }

        // now compare with manually downsampled version
        final float[] manualChannel0In = new float[channel0.length];
        for (int i=0; i<manualChannel0In.length; i++) {
            manualChannel0In[i] = channel0[i];
        }
        // first lowpass filter
        final FIRFilter lowpass = FIRFilter.createFir1_29thOrderLowpass(downFactor);
        final float[] manualChannel0Filtered = lowpass.filter(manualChannel0In);
        // then keep every third sample
        final int[] manualChannel0 = new int[channel0Result.length];
        for (int i=0; i<manualChannel0Filtered.length; i+=downFactor) {
            manualChannel0[i/downFactor] = Math.round(manualChannel0Filtered[i]);
        }
        // result should be the same as channel0Result
        for (int i=0; i<manualChannel0.length; i++) {
            assertEquals(manualChannel0[i], channel0Result[i]);
        }
    }

    @Test
    public void testDownsamplingWithOddFrames() {
        final int[] stereo = {0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000, 2000, 3000, 0, 1000};
        final int upFactor = 1;
        final int downFactor = 3;
        final Resampler stereoResampler = new Resampler(new Rational(upFactor, downFactor));
        stereoResampler.resample(stereo, new int[stereo.length / downFactor], 0, 2);
    }

    @Test
    public void testZeroOutput() {
        final Resampler stereoResampler = new Resampler(new Rational(1, 2));
        final int[] stereo = {0, 1000, 2000};
        stereoResampler.resample(stereo, new int[0], 0, 2);
    }
}
