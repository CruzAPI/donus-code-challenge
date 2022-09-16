package database.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import database.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "binary(16)")
	private UUID uuid;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private Role role;
	
	@ManyToMany(mappedBy = "roles", targetEntity = UserModel.class, cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<UserModel> users;
	
	@Override
	public RoleModel clone()
	{
		try
		{
			return (RoleModel) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
