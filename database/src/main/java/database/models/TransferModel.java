package database.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "binary(16)")
	private UUID uuid;
	
	@Column(columnDefinition = "decimal(19,4)", nullable = false)
	private BigDecimal amount;
	
	@Column(nullable = false)
	private LocalDateTime date;
	
	@ManyToOne(targetEntity = UserModel.class)
	@JoinColumn
	private UserModel fromUser;
	
	@ManyToOne(targetEntity = UserModel.class)
	@JoinColumn
	private UserModel toUser;
}