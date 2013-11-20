package com.doomonafireball.repmaxcalculator;

import com.google.inject.Injector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;

/**
 * User: wtauser Date: 9/6/12 Time: 3:56 PM
 */
@RunWith(RobolectricTestRunner.class)
public class DatastoreTest {

    Datastore datastore;

    @Before
    public void setup() {
        Injector i = RoboGuice.getBaseApplicationInjector(Robolectric.application);
        datastore = i.getInstance(Datastore.class);
    }

    @Test
    public void versionShouldPersist() {
        int testVersion = 12345;
        datastore.persistVersion(testVersion);

        Assert.assertEquals(testVersion, datastore.getVersion());
    }
}
