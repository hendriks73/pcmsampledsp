/*
 * =================================================
 * Copyright 2011 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.pcmsampledsp;

import org.junit.Test;

/**
 * TestRational.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestRational {

    @Test
    public void testValueOf() {
        final Rational rational = Rational.valueOf(160f / 147f);
        System.out.println(rational);
    }
}
