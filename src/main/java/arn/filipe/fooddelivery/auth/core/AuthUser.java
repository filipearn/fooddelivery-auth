package arn.filipe.fooddelivery.auth.core;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.Collection;
import java.util.Collections;

@Getter
public class AuthUser extends User {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String fullName;

    public AuthUser(arn.filipe.fooddelivery.auth.domain.User user, Collection<? extends GrantedAuthority> authorities){
        super(user.getEmail(), user.getPassword(), authorities);

        this.userId = user.getId();
        this.fullName = user.getName();
    }
}
