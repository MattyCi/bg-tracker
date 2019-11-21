package org.bgtrack.auth;import com.opensymphony.xwork2.ActionSupport;import org.apache.shiro.authc.AuthenticationException;import org.apache.shiro.subject.Subject;import org.bgtrack.models.user.Reguser;import org.bgtrack.utils.BGTConstants;public class ShiroBaseAction extends ActionSupport {	private static final long serialVersionUID = 6529231995619283129L;		protected transient Subject shiroUser;	protected Reguser regUser;		protected boolean isAuthenticationRequired = true;		@Override	public void validate() {				if (isAuthenticationRequired() && !isAuthenticated()) {						addActionError(BGTConstants.logInPromp);						throw new AuthenticationException("User must be logged in to perform action: " + this.getClass() );					}			}		public boolean isAuthenticated() {		return (this.shiroUser != null) && (this.shiroUser.isAuthenticated());	}		public boolean isPermitted(String permissionQuery) {				if ( this.getShiroUser().isPermitted(permissionQuery) ) {		    return true;		}				return false;			}	public Subject getShiroUser() {		return this.shiroUser;	}	public void setShiroUser(Subject shiroUser) {		this.shiroUser = shiroUser;	}		// used by the interceptor	public void setRegUser(Reguser regUser) {		this.regUser = regUser;	}		public Reguser getRegUser() {		return regUser;	}		public boolean isAuthenticationRequired() {		return isAuthenticationRequired;	}		public void setIsAuthenticationRequired(Boolean isAuthenticationRequired) {		 this.isAuthenticationRequired = isAuthenticationRequired;	}	}