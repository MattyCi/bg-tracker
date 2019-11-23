package org.bgtrack.models.user.authorization;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the permissions database table.
 * 
 */
@Entity
@Table(name="permissions")
@NamedQuery(name="Permission.findAll", query="SELECT p FROM Permission p")
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PERM_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int permId;

	@Column(name="PERM_VALUE")
	private String permValue;

	//bi-directional many-to-one association to RolesPermission
	@OneToMany(mappedBy="permission")
	private List<RolesPermission> rolesPermissions;

	//bi-directional many-to-one association to UserPermission
	@OneToMany(mappedBy="permission")
	private List<UserPermission> userPermissions;

	public Permission() {
	}

	public int getPermId() {
		return this.permId;
	}

	public void setPermId(int permId) {
		this.permId = permId;
	}

	public String getPermValue() {
		return this.permValue;
	}

	public void setPermValue(String permValue) {
		this.permValue = permValue;
	}

	public List<RolesPermission> getRolesPermissions() {
		return this.rolesPermissions;
	}

	public void setRolesPermissions(List<RolesPermission> rolesPermissions) {
		this.rolesPermissions = rolesPermissions;
	}

	public RolesPermission addRolesPermission(RolesPermission rolesPermission) {
		getRolesPermissions().add(rolesPermission);
		rolesPermission.setPermission(this);

		return rolesPermission;
	}

	public RolesPermission removeRolesPermission(RolesPermission rolesPermission) {
		getRolesPermissions().remove(rolesPermission);
		rolesPermission.setPermission(null);

		return rolesPermission;
	}

	public List<UserPermission> getUserPermissions() {
		return this.userPermissions;
	}

	public void setUserPermissions(List<UserPermission> userPermissions) {
		this.userPermissions = userPermissions;
	}

	public UserPermission addUserPermission(UserPermission userPermission) {
		getUserPermissions().add(userPermission);
		userPermission.setPermission(this);

		return userPermission;
	}

	public UserPermission removeUserPermission(UserPermission userPermission) {
		getUserPermissions().remove(userPermission);
		userPermission.setPermission(null);

		return userPermission;
	}

}