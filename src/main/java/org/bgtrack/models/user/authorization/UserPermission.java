package org.bgtrack.models.user.authorization;

import java.io.Serializable;
import javax.persistence.*;

import org.bgtrack.models.user.Reguser;


/**
 * The persistent class for the user_permissions database table.
 * 
 */
@Entity
@Table(name="user_permissions")
@NamedQuery(name="UserPermission.findAll", query="SELECT u FROM UserPermission u")
public class UserPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="USER_PERM_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int userPermId;

	//bi-directional many-to-one association to Reguser
	@ManyToOne
	@JoinColumn(name="USER_ID")
	private Reguser user;

	//bi-directional many-to-one association to Permission
	@ManyToOne
	@JoinColumn(name="PERM_ID")
	private Permission permission;

	public UserPermission() {
	}

	public int getUserPermId() {
		return this.userPermId;
	}

	public void setUserPermId(int userPermId) {
		this.userPermId = userPermId;
	}

	public Reguser getUser() {
		return this.user;
	}

	public void setUser(Reguser user) {
		this.user = user;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

}