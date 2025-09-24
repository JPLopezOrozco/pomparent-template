package com.juan.authservice.service;

import com.juan.authservice.model.MyUserDetails;
import com.juan.authservice.model.UserCredential;
import com.juan.authservice.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = Normalizer
                .normalize(username, Normalizer.Form.NFC)
                .trim()
                .toLowerCase(Locale.ROOT);
        UserCredential userCredential = repository.findByEmail(normalized);
        if (userCredential == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MyUserDetails(userCredential);
    }
}
