package org.bgtrack.models.user.authorization;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name="roles")
@NamedQuery(name="Role.findAll", query="SELECT r FROM Role r")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ROLE_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int roleId;

	private String descr;

	private String name;

	//bi-directional many-to-one association to RolesPermission
	@OneToMany(mappedBy="role")
	private List<RolesPermission> rolesPermissions;

	//bi-directional many-to-one association to UserRole
	@OneToMany(mappedBy="role")
	private List<UserRole> userRoles;

	public Role() {
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getDescr() {
		return this.descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RolesPermission> getRolesPermissions() {
		return this.rolesPermissions;
	}

	public void setRolesPermissions(List<RolesPermission> rolesPermissions) {
		this.rolesPermissions = rolesPermissions;
	}

	public RolesPermission addRolesPermission(RolesPermission rolesPermission) {
		getRolesPermissions().add(rolesPermission);
		rolesPermission.setRole(this);

		return rolesPermission;
	}

	public RolesPermission removeRolesPermission(RolesPermission rolesPermission) {
		getRolesPermissions().remove(rolesPermission);
		rolesPermission.setRole(null);

		return rolesPermission;
	}

	public List<UserRole> getUserRoles() {
		return this.userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	public UserRole addUserRole(UserRole userRole) {
		getUserRoles().add(userRole);
		userRole.setRole(this);

		return userRole;
	}

	public UserRole removeUserRole(UserRole userRole) {
		getUserRoles().remove(userRole);
		userRole.setRole(null);

		return userRole;
	}

}