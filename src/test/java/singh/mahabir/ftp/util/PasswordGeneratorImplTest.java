/**
 * All rights reserved.
 */

package singh.mahabir.ftp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import singh.mahabir.ftp.configuration.BCryptPasswordEncoderConfiguration;

/**
 * @author Mahabir Singh
 *
 */
@SpringBootTest(classes = { PasswordGeneratorImpl.class, BCryptPasswordEncoderConfiguration.class })
@Slf4j
public class PasswordGeneratorImplTest {

    @Autowired
    IPasswordGenerator passwordGenerator;

    @Value("${ftp.password.isSpecialCharacterUsed}")
    private String isSpecial;

    @Value("${ftp.password.length}")
    private Integer length;

    @Value("${ftp.password.specialcharacter}")
    private String specialcharacter;

    @Test
    public void ismatches() {
	log.info("\n ismatches");
	String rawPassword = new String(passwordGenerator.generateTemporaryPassword());
	log.info("temporary password: {}", rawPassword);
	String hashedPassword = passwordGenerator.generateHashedPassword(rawPassword);
	assertThat(hashedPassword).hasSize(60);
	assertTrue(passwordGenerator.isMatches(rawPassword, hashedPassword));
	assertFalse(
		passwordGenerator.isMatches(new String(passwordGenerator.generateTemporaryPassword()), hashedPassword));
	log.info("\n password {}", passwordGenerator.generateHashedPassword("msdeo4u"));

    }

    @Test
    @org.springframework.test.context.junit.jupiter.EnabledIf(value = "#{'${ftp.password.isSpecialCharacterUsed}'=='true'}", loadContext = true)
    public void passwordGeneratorWithSpecialCharacter() {
	log.info("passwordGeneratorWithSpecialCharacter");
	String generateTemporaryPassword = new String(passwordGenerator.generateTemporaryPassword());
	log.info("temporary password: {}", generateTemporaryPassword);

	validateCommonCharacter(generateTemporaryPassword);

	Pattern special = Pattern.compile("[!@#$%^&*]");
	Matcher hasSpecial = special.matcher(generateTemporaryPassword);
	assertTrue(hasSpecial.find());
    }

    @Test
    @org.springframework.test.context.junit.jupiter.EnabledIf(value = "#{'${ftp.password.isSpecialCharacterUsed}'=='false'}", loadContext = true)
    public void passwordGeneratorWithOutSpecialCharacter() {
	log.info("\n passwordGeneratorWithOutSpecialCharacter");
	String generateTemporaryPassword = new String(passwordGenerator.generateTemporaryPassword());
	log.info("temporary password: {}", generateTemporaryPassword);

	validateCommonCharacter(generateTemporaryPassword);

	log.info("make test falied if it contains special character : {}");
	for (char c : generateTemporaryPassword.toCharArray()) {
	    assertThat(Character.isLetterOrDigit(c)).isTrue();
	}
    }

    /**
     * @param generateTemporaryPassword
     */
    private void validateCommonCharacter(String generateTemporaryPassword) {
	assertThat(generateTemporaryPassword).hasSize(length);

	Pattern small = Pattern.compile("[a-z]");
	Pattern upper = Pattern.compile("[A-Z]");
	Pattern number = Pattern.compile("[0-9]");

	Matcher hasSmallLetter = small.matcher(generateTemporaryPassword);
	Matcher hasUpperLetter = upper.matcher(generateTemporaryPassword);
	Matcher hasNumberLetter = number.matcher(generateTemporaryPassword);

	assertTrue(hasUpperLetter.find());
	assertTrue(hasSmallLetter.find());
	assertTrue(hasNumberLetter.find());
    }
}
