package me.loper.storage.sql;

import junit.framework.TestCase;
import me.loper.storage.nosql.redis.serializer.JsonByteBufferSerializer;

import java.util.Objects;
import java.util.UUID;

public class JsonByteBufferSerializerTest extends TestCase {

    public void testCorrectlyDeserialize() {
        TestUser u = new TestUser(UUID.randomUUID(), "testUser");
        JsonByteBufferSerializer<TestUser> serializer = new JsonByteBufferSerializer<>(TestUser.class);

        TestUser result = serializer.decode(serializer.encode(u));

        assertEquals(u, result);
    }

    private static class TestUser {
        public UUID userId;
        public String username;

        public TestUser(UUID userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TestUser testUser = (TestUser) o;

            return Objects.equals(userId, testUser.userId)
                && Objects.equals(username, testUser.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, username);
        }
    }
}
