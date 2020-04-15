/**
 * All rights reserved.
 */

package singh.mahabir.ftp.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahabir Singh
 *
 */

@Entity
@Table(name = "oauth_client")
@Setter
@Getter
public class Client {

    @Id
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    @Column(nullable = false)
    private String resourceIds;

    @Column(nullable = false)
    private boolean scoped;

    @Column(nullable = false)
    private String scope;

    @Column(nullable = false)
    private boolean secretRequired;

    private String authorizedGrantTypes;

    private String authorities;

    private Integer accessTokenValiditySeconds;

    private Integer refreshTokenValiditySeconds;

    private boolean autoApprove;
}
