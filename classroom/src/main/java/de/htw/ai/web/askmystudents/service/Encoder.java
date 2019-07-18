package de.htw.ai.web.askmystudents.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Encoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(final String toEncode) {
        final String encode = this.encoder.encode(toEncode);
        return encode;
    }

    public boolean matchingStrings(final String rawString, String encodedString) {
        encodedString = encodedString.replace('!', '/');
        final boolean matches = this.encoder.matches(rawString, encodedString);
        return matches;
    }

}
