package org.bgtrack.models.user.authorization;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the roles_permissions database table.
 * 
 */
@Entity
@Table(name="roles_permissions")
@NamedQuery(name="RolesPermission.findAll", query="SELECT r FROM RolesPermission r")
public class RolesPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ROLE_PERM_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int rolePermId;

	//bi-directional many-to-one association to Role
	@ManyToOne
	@JoinColumn(name="ROLE_ID")
	private Role role;

	//bi-directional many-to-one association to Permission
	@ManyToOne
	@JoinColumn(name="PERM_ID")
	private Permission permission;

	public RolesPermission() {
	}

	public int getRolePermId() {
		return this.rolePermId;
	}

	public void setRolePermId(int rolePermId) {
		this.rolePermId = rolePermId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

}