package net.sf.anathema.lib.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for net.sf.anathema.lib.test"); //$NON-NLS-1$
    //$JUnit-BEGIN$
    suite.addTestSuite(RangedAreaTest.class);
    suite.addTestSuite(RangeTest.class);
    //$JUnit-END$
    return suite;
  }
}