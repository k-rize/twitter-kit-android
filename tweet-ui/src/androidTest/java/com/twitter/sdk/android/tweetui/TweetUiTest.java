package com.twitter.sdk.android.tweetui;

import io.fabric.sdk.android.FabricTestUtils;
import io.fabric.sdk.android.KitStub;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;

import com.twitter.sdk.android.core.TwitterAndroidTestCase;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;

public class TweetUiTest extends TwitterAndroidTestCase {

    private static final String ANY_CLIENT_NAME = "client";

    private TweetUi tweetUi;
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        tweetUi = new TweetUi();
        FabricTestUtils.with(getContext(),
                new TwitterCore(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET)), tweetUi);
    }

    @Override
    protected void tearDown() throws Exception {
        FabricTestUtils.resetFabric();
        super.tearDown();
    }

    public void testGetVersion() {
        assertEquals(BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER,
                tweetUi.getVersion());
    }

    public void testTwitterDependency() {
        FabricTestUtils.resetFabric();
        try {
            FabricTestUtils.with(getContext(), new TweetUi());
            fail("UnmetDependencyException was expected");
        } catch (Exception ex) {
            if (!(ex instanceof UnmetDependencyException)) {
                fail();
            }
        }
    }

    public void testGetIdentifier() {
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, tweetUi.getIdentifier());
    }

    public void testGetInstance_tweetUiStarted() {
        try {
            final TweetUi instance = TweetUi.getInstance();
            assertNotNull(instance);
        } catch (Exception ex) {
            fail("IllegalStateException was expected");
        }
    }

    public void testGetInstance_tweetUiNotStarted() {
        FabricTestUtils.resetFabric();
        try {
            FabricTestUtils.with(getContext(), new KitStub());
            TweetUi.getInstance();
            fail("IllegalStateException was expected");
        } catch (Exception ex) {
            if (!(ex instanceof IllegalStateException)) {
                fail("IllegalStateException was expected");
            }
        } finally {
            FabricTestUtils.resetFabric();
        }
    }

    public void testScribe_scribeClientNull() {
        final EventNamespace ns = new EventNamespace.Builder().setClient(ANY_CLIENT_NAME).builder();

        try {
            tweetUi.scribeClient = null;
            tweetUi.scribe(ns, ns);
        } catch (NullPointerException e) {
            fail("should have gracefully ignored events");
        }
    }
}
