package org.bgtrack.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.bgtrack.models.user.daos.UserDAO;

import org.bgtrack.models.user.Reguser;

/**
 * Custom Shiro Realm configured for hashed and salted passwords, and a custom authorization implementation.
 * @author Matt
 */
public class BGTrackRealm extends JdbcRealm {
	private static final Logger LOG = LogManager.getLogger(BGTrackRealm.class);
	    
    protected static final String USER_PERMISSIONS_QUERY = "select perm_value from permissions perm "
    		+ "inner join user_permissions uperm on uperm.perm_id = perm.perm_id "
			+ "where uperm.user_id = ?";
    
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException {
		
		UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
		final String username = userPassToken.getUsername();

		if (username != null) {
			final Reguser user = UserDAO.getUserByUsername(username);

			if (user == null) {
				LOG.info(username + " does not exist in the databse!");
				return null;
			}

			// return salted credentials
			SaltedAuthenticationInfo info = 
					new UserCredSalt(username, user.getPassword(), user.getSalt());
			return info;
		} else {
			LOG.info("Username is null.");
			return null;
		}
	}
	
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = (String) getAvailablePrincipal(principals);
        
        String userId = UserDAO.getUserByUsername(username).getUserId();

        Connection conn = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            conn = dataSource.getConnection();

            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(conn, userId);
            
            if (permissionsLookupEnabled) {
            	
                permissions = getPermissions(conn, userId, roleNames);
                
                permissions = getUserSpecificPermissions(conn, userId, permissions);
                
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authorizing user [" + username + "]";
            if (LOG.isErrorEnabled()) {
            	LOG.error(message, e);
            }

            // Rethrow any SQL errors as an authorization exception
            throw new AuthorizationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;

    }

	private Set<String> getUserSpecificPermissions(Connection conn, String userId, Set<String> permissions) throws SQLException {
		
		if (permissions == null) {
			permissions = new LinkedHashSet<String>();
		}
		
		PreparedStatement ps = null;

		try {
			
			ps = conn.prepareStatement(USER_PERMISSIONS_QUERY);

			ps.setString(1, userId);

			ResultSet rs = null;

			try {

				rs = ps.executeQuery();

				while (rs.next()) {

					String permissionString = rs.getString(1);

					permissions.add(permissionString);

				}

			} finally {
				JdbcUtils.closeResultSet(rs);
			}
		} finally {
			JdbcUtils.closeStatement(ps);
		}

		return permissions;
	}
    
}
