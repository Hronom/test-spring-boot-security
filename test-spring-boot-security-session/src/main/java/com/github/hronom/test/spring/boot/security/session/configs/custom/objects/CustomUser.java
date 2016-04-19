package com.github.hronom.test.spring.boot.security.session.configs.custom.objects;

import com.sun.security.auth.UserPrincipal;

import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class CustomUser implements Principal {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUser(
        String usernameArg,
        Collection<? extends GrantedAuthority> authoritiesArg
    ) {
        username = usernameArg;
        authorities = authoritiesArg;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Compares this principal to the specified object.
     *
     * @param object The object to compare this principal against.
     * @return true if they are equal; false otherwise.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof UserPrincipal) {
            return username.equals(((UserPrincipal) object).getName());
        }
        return false;
    }

    /**
     * Returns a hash code for this principal.
     *
     * @return The principal's hash code.
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }

    /**
     * Returns the name of this principal.
     *
     * @return The principal's name.
     */
    @Override
    public String getName() {
        return username;
    }

    /**
     * Returns a string representation of this principal.
     *
     * @return The principal's name.
     */
    @Override
    public String toString() {
        return username;
    }
}
