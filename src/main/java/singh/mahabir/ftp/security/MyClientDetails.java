/**
 * All rights reserved.
 */

package singh.mahabir.ftp.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import singh.mahabir.ftp.repository.entity.Client;

/**
 * @author Mahabir Singh
 *
 */
public class MyClientDetails implements ClientDetails {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String clientSecret;
    private boolean scoped;
    private boolean secretRequired;
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
    private boolean autoApprove;

    private Set<String> resourceIds;
    private Set<String> scope;
    private Set<String> authorizedGrantTypes;
    private Collection<GrantedAuthority> authorities;

    public MyClientDetails(Client client) {
	clientId = client.getClientId();
	clientSecret = client.getClientSecret();
	accessTokenValiditySeconds = client.getAccessTokenValiditySeconds();
	autoApprove = client.isAutoApprove();
	refreshTokenValiditySeconds = client.getRefreshTokenValiditySeconds();
	scoped = client.isScoped();
	secretRequired = client.isSecretRequired();

	this.authorities = Arrays.stream(client.getAuthorities().split(","))
		.map(SimpleGrantedAuthority::new)
		.collect(Collectors.toList());
	scope = Arrays.stream(client.getScope().split(","))
		.collect(Collectors.toSet());

	authorizedGrantTypes = Arrays.stream(client.getAuthorizedGrantTypes().split(","))
		.collect(Collectors.toSet());

	resourceIds = Arrays.stream(client.getResourceIds().split(","))
		.collect(Collectors.toSet());
    }

    @Override
    public String getClientId() {
	return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
	return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
	return secretRequired;
    }

    @Override
    public String getClientSecret() {
	return clientSecret;
    }

    @Override
    public boolean isScoped() {
	return scoped;
    }

    @Override
    public Set<String> getScope() {
	return scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
	return authorizedGrantTypes;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
	return authorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
	return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
	return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
	return autoApprove;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
	return Collections.emptyMap();
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
	return Collections.emptySet();
    }

}
