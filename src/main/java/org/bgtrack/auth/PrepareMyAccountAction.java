package org.bgtrack.auth;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.models.user.AccountRedeemToken;
import org.bgtrack.models.user.daos.UserDAO;

public class PrepareMyAccountAction extends ShiroBaseAction {
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LogManager.getLogger(PrepareMyAccountAction.class);
	
	private List<AccountRedeemToken> createdTokens;
	
	@Override
	public boolean isAuthenticationRequired() {
		return true;
	}
	
	@Override
	public String execute() throws Exception {
		
		createdTokens = UserDAO.getAccountRedeemTokensUserCreated(regUser.getUserId());
		
		if (createdTokens == null) {
			LOG.debug("{} has no account redeem tokens for other users", shiroUser.getPrincipal());
		} else {
			LOG.debug("{} has {} account redeem tokens for other users", shiroUser.getPrincipal(), createdTokens.size());
		}
		
		return SUCCESS;
		
	}

	public List<AccountRedeemToken> getCreatedTokens() {
		return createdTokens;
	}

}
