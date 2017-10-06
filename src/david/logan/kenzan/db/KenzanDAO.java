package david.logan.kenzan.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//
// Status values of 0 and 1 are hard coded and assumed
//

@Repository
@Transactional(readOnly = false)
public class KenzanDAO {
	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional(readOnly = true)
	public Employee getEmployeeByUsername(String username, String password)
	{
		try
		{
			Employee retEmp = (Employee)entityManager.createQuery("SELECT e FROM Employee e where username = :username and bStatus = :status")
					.setParameter("username",  username)
					.setParameter("status",  Status.ACTIVE)
					.getSingleResult();
			if(retEmp.checkPassword(password))
			{
				Hibernate.initialize(retEmp.getRoles());	// Make sure the roles are loaded
				return retEmp;
			}
			else
				return null;
		} catch(NoResultException e)
		{
			return null;
		}
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Employee> getEmployees()
	{
		try {
			return entityManager.createQuery("SELECT e FROM Employee e where bStatus = :status").
					setParameter("status",  Status.ACTIVE).getResultList();
		} catch(NoResultException e) {
			return new ArrayList<Employee>();
		}
	}
	
	public Employee getEmployee(int id)
	{
		try {
			Employee sigh = (Employee) entityManager.createQuery("SELECT e FROM Employee e where id=:id and bStatus = :status")
					.setParameter("id", id)
					.setParameter("status", Status.ACTIVE)
					.getSingleResult();
			return sigh;
		} catch(NoResultException e)
		{
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public int addEmployee(Employee e)
	{
		Employee newEmployee = new Employee();
		newEmployee.setbStatus(e.getbStatus());
		newEmployee.setDateOfBirth(e.getDateOfBirth());
		newEmployee.setDateOfEmployment(e.getDateOfEmployment());
		newEmployee.setFirstName(e.getFirstName());
		newEmployee.setLastName(e.getLastName());
		newEmployee.setMiddleInitial(e.getMiddleInitial());
		newEmployee.setUsername(e.getUsername());
		entityManager.persist(newEmployee);
		entityManager.flush();
		return newEmployee.getId();
	}
	
	@Transactional(readOnly = false)
	public boolean updateEmployee(Employee e)
	{
		try
		{
			Employee exists = (Employee) entityManager.createQuery("SELECT e FROM Employee e where id=:id and bStatus = :status")
					.setParameter("id", e.getId())
					.setParameter("status", Status.ACTIVE)
					.getSingleResult();
			exists.setbStatus(e.getbStatus());
			exists.setDateOfBirth(e.getDateOfBirth());
			exists.setDateOfEmployment(e.getDateOfEmployment());
			exists.setFirstName(e.getFirstName());
			exists.setLastName(e.getLastName());
			exists.setMiddleInitial(e.getMiddleInitial());
			exists.setUsername(e.getUsername());
			entityManager.merge(exists);
			return true;
		} catch(NoResultException ee)
		{
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean deleteEmployee(Employee e)
	{
		try
		{
			Employee exists = (Employee) entityManager.createQuery("SELECT e FROM Employee e where id=:id and bStatus = :status")
					.setParameter("id", e.getId())
					.setParameter("status", Status.ACTIVE)
					.getSingleResult();
			exists.setbStatus(Status.INACTIVE);
			entityManager.merge(exists);
			return true;
		} catch(NoResultException ee)
		{
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean deleteEmployee(int id)
	{
		int updated = entityManager.createQuery("UPDATE Employee set bStatus=:inactive_status where id=:id and bStatus=:active_status")
		.setParameter("id",  id)
		.setParameter("active_status",  Status.ACTIVE)
		.setParameter("inactive_status",  Status.INACTIVE)
		.executeUpdate();
		return (updated != 0);
	}
}
