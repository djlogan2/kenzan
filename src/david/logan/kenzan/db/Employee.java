package david.logan.kenzan.db;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

//
//	The employee class. It contains the business requirements, along with
//	three others required for access security:
//		username - Yes, we could have used some type of "firstname middleinit lastname", but I think a "token" such as username is far more efficient.
//		password - A bcrypt (one way encryption) password
//		many to many set of roles
//	I allowed the username field to be added and updated from the endpoint APIs, mostly because I am disallowing a blank username in the database.
//		Password can be null. The user can also have no roles.
//
@Table(name="employee")
@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	//@NotNull
	@JsonProperty("_id")
	private Integer id;

	@Column(nullable = false)
	@NotNull
	private String firstName;
	
	@Column(nullable = true)
	private Character middleInitial;
	
	@Column(nullable = false)
	@NotNull
	private String lastName;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone="America/Denver")
	@Column(nullable = false)
	@NotNull
	private Calendar dateOfBirth;
	
	@Column(nullable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "America/Denver")
	private Calendar dateOfEmployment;
	
	@Column(nullable = false)
	@NotNull
	private Status bStatus;
	
	@Column(nullable = false)
	@NotNull
	private String username;

	@JsonIgnore
	private String password;
	
	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "employee_role_join", joinColumns = { @JoinColumn(name = "employee_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	Set<EmployeeRole> roles;
	
	public Employee() {}
	
	public Employee(String firstName, char middleInitial, String lastName, Calendar dateOfBirth, Calendar dateOfEmployment, Status bStatus)
	{
		this.bStatus = bStatus;
		this.dateOfBirth = dateOfBirth;
		this.dateOfEmployment = dateOfEmployment;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Character getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(Character middleInitial) {
		this.middleInitial = middleInitial;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Calendar getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Calendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Calendar getDateOfEmployment() {
		return dateOfEmployment;
	}
	public void setDateOfEmployment(Calendar dateOfEmployment) {
		this.dateOfEmployment = dateOfEmployment;
	}
	public Status getbStatus() {
		return bStatus;
	}
	public void setbStatus(Status bStatus) {
		this.bStatus = bStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String cleartext)
	{
		password = BCrypt.hashpw(cleartext, BCrypt.gensalt());
	}
	
	public boolean checkPassword(String cleartext)
	{
		try
		{
		return BCrypt.checkpw(cleartext, password);
		} catch(IllegalArgumentException e)
		{
			return false;
		}
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Set<EmployeeRole> getRoles() { return roles; }
	
	@Override
	public boolean equals(Object _other)
	{
		if(!(_other instanceof Employee))
			return false;
		Employee other = (Employee)_other;
		return (
				other.id == this.id
				// Should we check other fields?
				);
	}

	public boolean isIdNull() {
		return (id == null);
	}
}
