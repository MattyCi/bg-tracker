package org.bgtrack.auth;

import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

public class UserCredSalt implements SaltedAuthenticationInfo {
	private static final long serialVersionUID = -9103536123714371398L;
	
	private final String username;
	private final String password;
	private final String salt;

	public UserCredSalt(String username, String password, String salt) {
		this.username = username;
		this.password = password;
		this.salt = salt;
	}

	public PrincipalCollection getPrincipals() {
		PrincipalCollection coll = new SimplePrincipalCollection(username, username);
		return coll;
	}

	public Object getCredentials() {
		return password;
	}

	public ByteSource getCredentialsSalt() {
		return new SimpleByteSource(Base64.decode(salt));
	}

}
