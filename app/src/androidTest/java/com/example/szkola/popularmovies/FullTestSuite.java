package com.example.szkola.popularmovies;

/**
 * FullTest Suite class used in Sunshine-Version-2 project by Udacity
 */
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }
    public FullTestSuite() {
        super();
    }
}
