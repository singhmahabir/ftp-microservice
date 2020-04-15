///**
// * All rights reserved.
// */
//
//package singh.mahabir.ftp.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
///**
// * @author Mahabir Singh
// *
// */
////@Configuration
////@EnableAuthorizationServer
//public class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {
//
//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private PasswordEncoder encoder;
//
////    @Autowired
////    DataSource dataSource;
//
//    @Autowired
//    private ClientDetailsService clientDetailsService;
//
//    @Value("${oauth2.jwt.clientId:oath2Client}")
//    private String clientId;
//
//    @Value("${oauth2.jwt.client-secret:secret}")
//    private String clientSecret;
//
//    @Value("${oauth2.jwt.signing-key:123}")
//    private String jwtSigningKey;
//
//    @Value("${oauth2.jwt.scope:read,write}")
//    private String[] scope;
//
//    @Value("${oauth2.jwt.authorities:ROLE_CLIENT,ROLE_TRUSTED_CLIENT,ROLE_USER,ROLE_ADMIN}")
//    private String[] authorities;
//
//    @Value("${oauth2.jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
//    private String[] authorizedGrantTypes;
//
//    @Value("${oauth2.jwt.accessTokenValidititySeconds:180}") // 12 hours
//    private int accessTokenValiditySeconds;
//
//    @Value("${oauth2.jwt.refreshTokenValiditySeconds:600}") // 30 days
//    private int refreshTokenValiditySeconds;
//
//    @Override
//    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//	oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").passwordEncoder(encoder);
//    }
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
////	clients.jdbc(dataSource);
//	clients.withClientDetails(clientDetailsService);
//
////	JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
////	  clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
////	  clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
////	  clientDetailsService.setPasswordEncoder(encoder);
////	  clientDetailsService.
////	  clients.withClientDetails(clientDetailsService);
////	clients.inMemory()
////		.withClient(clientId)
////		.secret(encoder.encode(clientSecret))
////		.authorizedGrantTypes(authorizedGrantTypes)
////		.scopes(scope)
////		.authorities(authorities)
////		.autoApprove(true)
////		.resourceIds(ResourceServerConfiguration.RESOURCE_ID)
////		.accessTokenValiditySeconds(accessTokenValiditySeconds)// 180 Access token is only valid for 3 minutes.
////		.refreshTokenValiditySeconds(refreshTokenValiditySeconds);
//	// 600 Refresh token is only valid for 10 minutes.;
//    }
//
//    @Override
//    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//	endpoints.tokenStore(tokenStore())
//		.authenticationManager(authenticationManager)
//		.accessTokenConverter(accessTokenConverter())
//		.userDetailsService(userDetailsService);
//    }
//
//    @Bean
//    public TokenStore tokenStore() {
//	return new JwtTokenStore(accessTokenConverter());
//    }
//
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//	JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//	converter.setSigningKey(jwtSigningKey);
//	return converter;
//    }
//}
