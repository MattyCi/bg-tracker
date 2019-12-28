package org.bgtrack.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.bgtrack.auth.ShiroBaseAction;
import org.bgtrack.models.user.daos.UserDAO;
import org.bgtrack.models.user.Reguser;

/**
 * Determines whether the current executing user is registered or not. It the
 * user is a guest, either retrieve or create a new record in the users table
 * depending on the cookies that exist from the client. Inserts the current
 * Reguser user into the value stack so that it can be injected into Struts 2
 * actions should they have a JavaBeans setter
 * <code>setRegUser(org.bgtrack.models.user.Reguser regUser)</code>.
 */
public class UserValidatorInterceptor implements Interceptor {
	private static final long serialVersionUID = -4919863537675303155L;
	
	private static final Logger LOG = LogManager.getLogger(UserValidatorInterceptor.class);

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {

		if ((actionInvocation.getAction() instanceof ShiroBaseAction)) {
			Subject shiroUser = SecurityUtils.getSubject();

			 // determine whether this is a guest or registered user if guest user
			if (shiroUser.getPrincipal() == null) {
				LOG.debug("Guest user is running command.");
			} else {
				Reguser regUser = UserDAO.getUserByEmail(shiroUser.getPrincipal().toString());
				
				LOG.debug("Registered user {} is running command.", regUser.getEmail());
				
				actionInvocation.getStack().setValue("regUser", regUser);
			}

			actionInvocation.getStack().setValue("shiroUser", shiroUser);
		}
		
		return actionInvocation.invoke();
	}
}
