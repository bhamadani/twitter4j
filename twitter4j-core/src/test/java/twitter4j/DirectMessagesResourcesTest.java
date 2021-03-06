/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package twitter4j;

import twitter4j.json.DataObjectFactory;

import java.util.Date;
import java.util.List;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
public class DirectMessagesResourcesTest extends TwitterTestBase {
    public DirectMessagesResourcesTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDirectMessages() throws Exception {
        String expectedReturn = new Date() + " < #test > &ほげほげ @t4j_news %& http://twitter4j.org/en/index.html#download pic.twitter.com/d4G7MQ62";
        DirectMessage dm = twitter1.sendDirectMessage(id3.id, expectedReturn);
        assertTrue(0 <= dm.getId());
        assertNotNull(DataObjectFactory.getRawJSON(dm));
        assertEquals(dm, DataObjectFactory.createDirectMessage(DataObjectFactory.getRawJSON(dm)));
        // urls are wrapped by t.co. thus assertEquals doesn't apply
        assertTrue(dm.getText().contains(" < #test > &ほげほげ @t4j_news %&"));
        assertEquals(id1.screenName, dm.getSender().getScreenName());
        assertEquals(id3.screenName, dm.getRecipient().getScreenName());


        assertTrue(dm.getUserMentionEntities().length == 1);
        UserMentionEntity userMentionEntity = dm.getUserMentionEntities()[0];
        assertEquals("t4j_news", userMentionEntity.getScreenName());
        assertEquals("@t4j_news", dm.getText().substring(userMentionEntity.getStart(), userMentionEntity.getEnd()));

        assertTrue(dm.getHashtagEntities().length == 1);
        HashtagEntity hashtagEntity = dm.getHashtagEntities()[0];
        assertEquals("test", hashtagEntity.getText());
        assertEquals("#test", dm.getText().substring(hashtagEntity.getStart(), hashtagEntity.getEnd()));

        assertTrue(dm.getMediaEntities().length == 1);
        MediaEntity mediaEntity = dm.getMediaEntities()[0];
        // XXXXX of pic.twitter.com/XXXXX is altered by the API. assertEquals("pic.twitter.com/d4G7MQ62".. doesn't apply
        assertTrue(mediaEntity.getDisplayURL().startsWith("pic.twitter.com/"));
        assertTrue(dm.getText().substring(mediaEntity.getStart(),mediaEntity.getEnd()).matches("http://t.co/[a-zA-Z0-9]+"));
        assertEquals("http://twitter.com/yusuke/status/268294645526708226/photo/1", mediaEntity.getExpandedURL());

        assertTrue(dm.getURLEntities().length == 1);
        URLEntity urlEntity = dm.getURLEntities()[0];
        assertEquals("twitter4j.org/en/index.html#…", urlEntity.getDisplayURL());
        assertTrue(dm.getText().substring(urlEntity.getStart(),urlEntity.getEnd()).matches("http://t.co/[a-zA-Z0-9]+"));
        assertEquals("http://twitter4j.org/en/index.html#download", urlEntity.getExpandedURL());

        List<DirectMessage> actualReturnList = twitter3.getDirectMessages();
        assertNotNull(DataObjectFactory.getRawJSON(actualReturnList));
        assertEquals(actualReturnList.get(0), DataObjectFactory.createDirectMessage(DataObjectFactory.getRawJSON(actualReturnList.get(0))));
        assertTrue(1 <= actualReturnList.size());
        try {
            dm = twitter1.showDirectMessage(actualReturnList.get(0).getId());
        } catch (TwitterException te) {
            // twitter1 is not allowed to access or delete your direct messages
            assertEquals(403, te.getStatusCode());
        }
        dm = twitter3.showDirectMessage(actualReturnList.get(0).getId());
        assertNotNull(DataObjectFactory.getRawJSON(dm));
        assertEquals(dm, DataObjectFactory.createDirectMessage(DataObjectFactory.getRawJSON(dm)));
        assertEquals(actualReturnList.get(0).getId(), dm.getId());
    }
}
