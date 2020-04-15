///**
// * All rights reserved.
// */
//
//package singh.mahabir.ftp.security;
//
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//
///**
// * @author Mahabir Singh
// *
// */
////@Configuration
////@EnableResourceServer
//public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//    public static final String RESOURCE_ID = "api";
//    private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
//    private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
//    private static final String SECURED_PATTERN = "/secured/**";
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) {
//	resources.resourceId(RESOURCE_ID);
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//	/*
//	 * http.requestMatchers()
//	 * .antMatchers(SECURED_PATTERN).and().authorizeRequests()
//	 * .antMatchers(HttpMethod.POST, SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
//	 * .anyRequest().access(SECURED_READ_SCOPE);
//	 */
////	http.antMatcher("/album/**")
////		.authorizeRequests()
////		.anyRequest()
////		.authenticated();
////	http
////		.authorizeRequests()
////		.antMatchers("/oauth/token", "/h2-console/**", "/swagger-ui.html/**")
////		.permitAll()
////		.anyRequest()
////		.authenticated()
////		.and()
////		.formLogin();
////	http.headers().frameOptions().disable();
//
//	http.csrf()
//		.disable()
//		.sessionManagement()
//		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//		.and()
//		.authorizeRequests()
//		.antMatchers("/oauth/token", "/h2-console/**", "/swagger-ui.html/**")
//		.permitAll()
//		.anyRequest()
//		.authenticated()
//		.and()
//		.formLogin();
//	http.headers().frameOptions().disable();
//    }
//}
