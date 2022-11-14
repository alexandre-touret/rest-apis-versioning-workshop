package info.touret.apiversionning.authorizationserver;

import java.util.Set;

public class ClientsConfigurationProperties{
    private String clientId;
    private String clientSecret;
    private Set<String> scopes;


	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public Set<String> getScopes() {
		return this.scopes;
	}

	public void setScopes(Set<String> scopes) {
		this.scopes = scopes;
	}


}