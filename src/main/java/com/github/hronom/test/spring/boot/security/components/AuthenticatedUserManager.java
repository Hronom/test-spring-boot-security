package com.github.hronom.test.spring.boot.security.components;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthenticatedUserManager {
    private final HashMap<String, Authentication> tokenToAuthentication = new HashMap<>();
    private final HashMap<Object, String> principalToToken = new HashMap<>();
    private final HashMap<String, Long> tokenToCreationTimeMs = new HashMap<>();

    private final long expireTime;

    public AuthenticatedUserManager(long expireTimeArg) {
        expireTime = expireTimeArg;
    }

    public void putToken(Authentication authentication) {
        while (true) {
            String token = String.valueOf(ThreadLocalRandom.current()
                .nextLong(Long.MIN_VALUE, (Long.MAX_VALUE - 1) + 1));
            if (!tokenToAuthentication.containsKey(token)) {
                tokenToAuthentication.put(token, authentication);
                principalToToken.put(authentication.getPrincipal(), token);
                tokenToCreationTimeMs.put(token, System.currentTimeMillis());
                return;
            }
        }
    }

    public Authentication getAuthentication(String token) {
        return tokenToAuthentication.get(token);
    }

    public String getToken(Authentication authentication) {
        return principalToToken.get(authentication.getPrincipal());
    }

    @Deprecated
    public void freeToken(Authentication authentication) {
        String token = principalToToken.remove(authentication.getPrincipal());
        tokenToAuthentication.remove(token);
        tokenToCreationTimeMs.remove(token);
    }

    @Scheduled(fixedDelay = 5000)
    private void checkExpiredTokens() {
        long currentTime = System.currentTimeMillis();

        LinkedList<String> tokensToRemove = new LinkedList<>();
        for (Map.Entry<String, Long> entry : tokenToCreationTimeMs.entrySet()) {
            if ((currentTime - entry.getValue()) > expireTime) {
                tokensToRemove.add(entry.getKey());
            }
        }

        for (String token : tokensToRemove) {
            Authentication authentication = tokenToAuthentication.get(token);
            freeToken(authentication);
            System.out.println(token + " removed!");
        }
    }
}
