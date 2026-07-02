package quizzy.util;

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PasswordUtilTest {

    @Test
    public void generateSaltProducesDecodableNonEmptyValue() {
        String salt = PasswordUtil.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
        assertEquals(16, Base64.getDecoder().decode(salt).length);
    }

    @Test
    public void generateSaltIsRandomPerCall() {
        assertNotEquals(PasswordUtil.generateSalt(), PasswordUtil.generateSalt());
    }

    @Test
    public void hashIsDeterministicForSameSaltAndPassword() {
        String salt = PasswordUtil.generateSalt();
        assertEquals(PasswordUtil.hash(salt, "hunter2"), PasswordUtil.hash(salt, "hunter2"));
    }

    @Test
    public void differentSaltsProduceDifferentHashesForSamePassword() {
        String hashA = PasswordUtil.hash(PasswordUtil.generateSalt(), "hunter2");
        String hashB = PasswordUtil.hash(PasswordUtil.generateSalt(), "hunter2");
        assertNotEquals(hashA, hashB);
    }

    @Test
    public void hashNeverEqualsPlaintext() {
        String salt = PasswordUtil.generateSalt();
        assertNotEquals("hunter2", PasswordUtil.hash(salt, "hunter2"));
    }

    @Test
    public void verifySucceedsForCorrectPassword() {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(salt, "correct horse");
        assertTrue(PasswordUtil.verify("correct horse", salt, hash));
    }

    @Test
    public void verifyFailsForWrongPassword() {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(salt, "correct horse");
        assertFalse(PasswordUtil.verify("wrong horse", salt, hash));
    }

    @Test
    public void verifyFailsWhenSaltDiffers() {
        String hash = PasswordUtil.hash(PasswordUtil.generateSalt(), "password");
        assertFalse(PasswordUtil.verify("password", PasswordUtil.generateSalt(), hash));
    }

    @Test
    public void verifyIsNullSafe() {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(salt, "password");
        assertFalse(PasswordUtil.verify(null, salt, hash));
        assertFalse(PasswordUtil.verify("password", null, hash));
        assertFalse(PasswordUtil.verify("password", salt, null));
    }
}
