package com.juan.monolithapp.service;

import com.juan.monolithapp.model.RefreshToken;
import com.juan.monolithapp.model.User;

import java.time.Duration;

public interface IRefreshToken {
    RefreshToken issue(User user, String rt);
    void revokeAllForUser(Long userId);
    RefreshToken validateActive(String token);
    void revoke(String token);
}
