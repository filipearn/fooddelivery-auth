package arn.filipe.fooddelivery.auth.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtKeyStoreProperties jwtKeyStoreProperties;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
//            .inMemory()
//                    .withClient("fooddelivery-web")
//                    .secret(passwordEncoder.encode("web123"))
//                    .authorizedGrantTypes("password", "refresh_token")
//                    .scopes("WRITE", "READ")
//                    .accessTokenValiditySeconds(60*60*6) //6 hrs
//                    .refreshTokenValiditySeconds(60*60*24*7) //7 days
//
//                .and()
//                    .withClient("revenues")
//                    .secret(passwordEncoder.encode("faturamento123"))
//                    .authorizedGrantTypes("client_credentials")
//                    .scopes("WRITE", "READ")
//
//
//                .and()
//                    .withClient("foodanalytics")
//                    .secret(passwordEncoder.encode("food123"))
//                    .authorizedGrantTypes("authorization_code")
//                    .scopes("WRITE", "READ")
//                    .redirectUris("http://application-client")
//
//                .and()
//
//                    .withClient("checktoken")
//                    .secret(passwordEncoder.encode("check123"));

    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

        //SYMMETRIC SIGN
        //jwtAccessTokenConverter.setSigningKey("1928371ASKDLAMSDOAISJDIUSHDNIAUSDHAISUH129391293");

        //To use jwt asymmetric sign with jks
        var jksResource = new ClassPathResource(jwtKeyStoreProperties.getPath());
//        var keyStorePass = "123456";
//        var keyPairAlias = "fooddelivery";

        var keyStoreKeyFactory = new KeyStoreKeyFactory(jksResource, jwtKeyStoreProperties.getPassword().toCharArray());
        var keyPair = keyStoreKeyFactory.getKeyPair(jwtKeyStoreProperties.getKeypairAlias());

        jwtAccessTokenConverter.setKeyPair(keyPair);

        return jwtAccessTokenConverter;
    }
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //security.checkTokenAccess("isAuthenticated()");
        security.checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        var enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(new JwtCustomClaimsTokenEnhancer(), jwtAccessTokenConverter()));

        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false)
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(enhancerChain)
                .approvalStore(approvalStore(endpoints.getTokenStore()))
                .tokenGranter(tokenGranter(endpoints));
    }

    private ApprovalStore approvalStore(TokenStore tokenStore){
        var approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore);

        return approvalStore;
    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        var pkceAuthorizationCodeTokenGranter = new PkceAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());

        var granters = Arrays.asList(
                pkceAuthorizationCodeTokenGranter, endpoints.getTokenGranter());

        return new CompositeTokenGranter(granters);
    }


}
