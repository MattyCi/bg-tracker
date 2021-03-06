package org.bgtrack.auth;

import com.opensymphony.xwork2.Preparable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.bgtrack.models.user.Reguser;
import org.bgtrack.models.user.daos.UserDAO;

public class ShiroLogInAction extends ShiroBaseAction implements Preparable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(ShiroLogInAction.class);
	
	private String username;
	private String password;
	private transient Subject shiroUser;
	Reguser regUser;

	@Override
	public boolean isAuthenticationRequired() {
		return false;
	}
	
	public String execute() {
		String result = "error";
		if (this.shiroUser != null) {
			
			if (!this.shiroUser.isAuthenticated()) {
				
				UsernamePasswordToken token = new UsernamePasswordToken(this.username, this.password);
				token.setRememberMe(true);
				try {
					
					this.shiroUser.login(token);
					this.setRegUser(UserDAO.getUserByUsername(shiroUser.getPrincipal().toString()));
					
					Session session = this.shiroUser.getSession();
					session.setAttribute("csrfToken", this.generateCSRFToken());
					
					result = "success";
					
				} catch (AuthenticationException ae) {
					
					addActionError("Either your username or password is incorrect.");
					
					LOG.info("User tried to log in as " + username + ", but the attempt failed.", ae);
					
				}
			} else if (this.shiroUser.isAuthenticated()) {
				
				this.setRegUser(UserDAO.getUserByUsername(shiroUser.getPrincipal().toString()));
				
				result = "success";
				
			}
		}
		return result;
	}

	public Subject getShiroUser() {
		return this.shiroUser;
	}

	public void setShiroUser(Subject shiroUser) {
		this.shiroUser = shiroUser;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Reguser getRegUser() {
		return regUser;
	}

	public void setRegUser(Reguser regUser) {
		this.regUser = regUser;
	}

	public void prepare() throws Exception {
		this.shiroUser = SecurityUtils.getSubject();
	}
}

