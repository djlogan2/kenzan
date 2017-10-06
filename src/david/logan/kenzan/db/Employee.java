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

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name="employee")
@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false)
	private String firstName;
	@Column(nullable = true)
	private char middleInitial;
	@Column(nullable = false)
	private String lastName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone="America/Denver")
	@Column(nullable = false)
	private Calendar dateOfBirth;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Denver")
	private Calendar dateOfEmployment;
	@Column(nullable = false)
	private Status bStatus;
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
	public char getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(char middleInitial) {
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
}
