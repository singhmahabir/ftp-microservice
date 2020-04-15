package singh.mahabir.ftp.util;

import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PasswordGeneratorImpl implements IPasswordGenerator {

    @Autowired
    private PasswordEncoder encoder;

    Random random = new Random();

    private static String specialcharacter;
    private static Integer length;
    private static boolean isSpecialCharacterUsed;

    private static final String CAPITAL_CASE_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_LETTER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    private static final String COMBINED_CHAR = CAPITAL_CASE_LETTER + LOWER_CASE_LETTER + NUMBERS;

    @PostConstruct
    public void init() {
	log.info("@PostConstruct PasswordGenerator for length:{} specialcharacter:{} isSpecialCharacterUsed:{}", length,
		specialcharacter, isSpecialCharacterUsed);
    }

    @Override
    public char[] generateTemporaryPassword() {
	log.info("Inside Generate Temporary password");
	char[] password = new char[length];

	password[0] = LOWER_CASE_LETTER.charAt(random.nextInt(LOWER_CASE_LETTER.length()));
	password[1] = CAPITAL_CASE_LETTER.charAt(random.nextInt(CAPITAL_CASE_LETTER.length()));
	password[2] = NUMBERS.charAt(random.nextInt(NUMBERS.length()));

	if (isSpecialCharacterUsed) {
	    password[3] = specialcharacter.charAt(random.nextInt(specialcharacter.length()));
	    String combined = COMBINED_CHAR + specialcharacter;
	    setRemainingPassword(4, password, combined);
	} else {
	    setRemainingPassword(3, password, COMBINED_CHAR);
	}
	return password;
    }

    private void setRemainingPassword(int start, char[] password, String combined) {
	for (int i = start; i < length; i++) {
	    password[i] = combined.charAt(random.nextInt(combined.length()));
	}
    }

    @Override
    public String generateHashedPassword(String rawPassword) {
	return encoder.encode(rawPassword);
    }

    @Override
    public boolean isMatches(String rawPassword, String storedPassword) {
	return encoder.matches(rawPassword, storedPassword);
    }

    @Value("${ftp.password.isSpecialCharacterUsed}")
    public void setSpecialCharacterUsed(boolean isSpecialCharacterUsed) {
	PasswordGeneratorImpl.isSpecialCharacterUsed = isSpecialCharacterUsed;
    }

    @Value("${ftp.password.specialcharacter}")
    public void setSpecialcharacter(String specialcharacter) {
	PasswordGeneratorImpl.specialcharacter = specialcharacter;
    }

    @Value("${ftp.password.length}")
    public void setLength(Integer length) {
	PasswordGeneratorImpl.length = length;
    }
}
