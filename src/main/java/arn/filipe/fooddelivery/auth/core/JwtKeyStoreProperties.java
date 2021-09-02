package arn.filipe.fooddelivery.auth.core;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Component
@ConfigurationProperties(value = "fooddelivery.jwt.keystore")
public class JwtKeyStoreProperties {

    @NotBlank
    private String path;

    @NotBlank
    private String password;

    @NotBlank
    private String keypairAlias;

    public String getPath() {
        return path;
    }

    public String getPassword() {
        return password;
    }

    public String getKeypairAlias() {
        return keypairAlias;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setKeypairAlias(String keypairAlias) {
        this.keypairAlias = keypairAlias;
    }
}