package database.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "binary(16)")
	private UUID uuid;
	
	@Column(columnDefinition = "char(11)", unique = true, nullable = false)
	private String cpf;
	
	@Column(columnDefinition = "decimal(19,4)", nullable = false)
	private BigDecimal balance = new BigDecimal("10000");
	
	@Column(columnDefinition = "varchar(100)", unique = true, nullable = false)
	private String username;
	
	@Column(columnDefinition = "binary(60)", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private String password;
	
	@Column(columnDefinition = "varchar(130)", nullable = false)
	private String fullName;
	
	@OneToMany(mappedBy = "user", targetEntity = DepositModel.class)
	@ToString.Exclude
	@JsonIgnore
	private List<DepositModel> deposits;
	
	@OneToMany(mappedBy = "fromUser", targetEntity = TransferModel.class)
	@ToString.Exclude
	@JsonIgnore
	private List<TransferModel> transfersSent;
	
	@OneToMany(mappedBy = "toUser", targetEntity = TransferModel.class)
	@ToString.Exclude
	@JsonIgnore
	private List<TransferModel> transfersReceived;
	
	@ManyToMany(targetEntity = RoleModel.class, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role")
	@ToString.Exclude
	@JsonIgnore
	private List<RoleModel> roles;
	
	@Override
	public UserModel clone()
	{
		try
		{
			return (UserModel) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}