package arn.filipe.fooddelivery.auth.core;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;


import java.util.Collections;

@Getter
public class AuthUser extends User {
    private static final long serialVersionUID = 1L;

    private String fullName;

    public AuthUser(arn.filipe.fooddelivery.auth.domain.User user){
        super(user.getEmail(), user.getPassword(), Collections.EMPTY_LIST);

        this.fullName = user.getName();
    }
}
