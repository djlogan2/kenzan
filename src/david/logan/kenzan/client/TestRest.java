package david.logan.kenzan.client;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import david.logan.kenzan.db.Employee;
import david.logan.kenzan.db.Status;

//
//	The test class.
//	It tests all of the standard stuff, not allowing various accesses based on roles,
//	not returning INACTIVE records, that sort of thing. 
public class TestRest {

	private static final String[] usernames = new String[] {"kenzan", "kenzana", "kenzand", "kenzanu", "kenzanad", "kenzanau", "kenzanadu", "kenzandu" };
	private HashMap<String, KenzanRestClient> clientMap = new HashMap<String, KenzanRestClient>();
	private int testno = 0;
	
	private int TestNo() { return testno++; } 

	private Employee newEmployee(String prefix)
	{
		return newEmployee(prefix, TestNo());
	}
	private Employee newEmployee(String prefix, int testno)
	{
		Employee e = new Employee();
		e.setbStatus(Status.ACTIVE);
		e.setDateOfBirth(Calendar.getInstance());
		e.setDateOfEmployment(Calendar.getInstance());
		e.setFirstName(prefix + " firstname " + testno);
		e.setLastName(prefix + " lastname " + testno);
		e.setMiddleInitial('M');
		e.setUsername(prefix + testno);
		e.setPassword("testpassword");
		return e;
	}

	public TestRest()
	{
		for(String username : usernames)
		{
			KenzanRestClient new_client = new KenzanRestClient();
			new_client.login(username,  "kenzan");
			clientMap.put(username,  new_client);
		}
	}
	
	@Test
	public void test_adds() {
		for(KenzanRestClient client : clientMap.values())
			assert(client.getUsername().replaceAll("kenzan", "").contains("a") == (client.addEmployee(newEmployee("test_adds")) != -1));
	}

	@Test
	public void test_inactive_status()
	{
		Employee emp = newEmployee("test_inactive_status");
		emp.setId(clientMap.get("kenzana").addEmployee(emp));
		assertNotEquals(-1, emp.getId());
		Employee check = clientMap.get("kenzana").getEmployee(emp.getId());
		assertNotNull(check);
		ArrayList<Employee> empList1 = clientMap.get("kenzana").getAllEmployees();
		assert(empList1 != null && empList1.size() > 0 && empList1.contains(emp));
		assertTrue(clientMap.get("kenzand").deleteEmployee(emp));
		check = clientMap.get("kenzana").getEmployee(emp.getId());
		assertNull(check);
		ArrayList<Employee> empList2 = clientMap.get("kenzana").getAllEmployees();
		assert(empList2 != null && empList2.size() > 0 && !empList2.contains(emp) && empList1.size() - 1 == empList2.size());
	}
	
	@Test
	public void test_duplicate_fails_and_successes()
	{
		Employee e = newEmployee("tdfas");
		// Should add
		e.setId(clientMap.get("kenzana").addEmployee(e));
		assertNotEquals(-1, e.getId());
		// Should be duplicate, because employee is active
		try
		{
			assertEquals(-1, clientMap.get("kenzana").addEmployee(e));
		} catch(RuntimeException re) {
			// TODO: We shouldn't be getting an exception, but for now, at least we are proving
			// that we cannot insert duplicate records.
		}
		// Set employee inactive, should work
		e.setbStatus(Status.INACTIVE);
		assertTrue(clientMap.get("kenzanu").updateEmployee(e));

		// Add another active employee now, it should work again
		e.setbStatus(Status.ACTIVE);
		assertNotEquals(-1, clientMap.get("kenzana").addEmployee(e));
	}
	
	@Test
	public void test_updates()
	{
		for(KenzanRestClient client : clientMap.values())
		{
			Employee employee = newEmployee("test_updates");
			Employee check;
			int id = clientMap.get("kenzana").addEmployee(employee) ;
			assert(id != -1);
			employee.setId(id);
			employee.setFirstName("Changed firstname test_updates " + employee.getId());
			boolean canupdate = client.getUsername().replaceAll("kenzan", "").contains("u");
			boolean didupdate = client.updateEmployee(employee);
			assertEquals(canupdate, didupdate);
			check = client.getEmployee(employee.getId());
			assertNotNull(check);
			assertEquals(employee.getbStatus(), check.getbStatus());
			assertEquals(employee.getDateOfBirth().getTimeZone(), check.getDateOfBirth().getTimeZone());
			assertEquals(employee.getDateOfBirth().get(Calendar.YEAR), check.getDateOfBirth().get(Calendar.YEAR));
			assertEquals(employee.getDateOfBirth().get(Calendar.MONTH), check.getDateOfBirth().get(Calendar.MONTH));
			assertEquals(employee.getDateOfBirth().get(Calendar.DATE), check.getDateOfBirth().get(Calendar.DATE));
			assertEquals(employee.getDateOfEmployment().getTimeZone(), check.getDateOfEmployment().getTimeZone());
			assertEquals(employee.getDateOfEmployment().get(Calendar.YEAR), check.getDateOfEmployment().get(Calendar.YEAR));
			assertEquals(employee.getDateOfEmployment().get(Calendar.MONTH), check.getDateOfEmployment().get(Calendar.MONTH));
			assertEquals(employee.getDateOfEmployment().get(Calendar.DATE), check.getDateOfEmployment().get(Calendar.DATE));
			assertEquals(employee.getMiddleInitial(), check.getMiddleInitial());
			assertEquals(employee.getLastName(), check.getLastName());
			assertEquals(employee.getUsername(), check.getUsername());
			assertEquals(employee.getId(), check.getId());
			if(canupdate)
				assertEquals(employee.getFirstName(), check.getFirstName());
			else
				assertNotEquals(employee.getFirstName(), check.getFirstName());
		}
	}
	
	@Test
	public void test_deletes()
	{
		for(KenzanRestClient client : clientMap.values())
		{
			Employee employee = newEmployee("test_deletes");
			Employee check;
			int id = clientMap.get("kenzana").addEmployee(employee) ;
			assert(id != -1);
			employee.setId(id);
			boolean success = client.deleteEmployee(employee);
			check = client.getEmployee(employee.getId());
			if(client.getUsername().replaceAll("kenzan", "").contains("d"))
			{
				assertTrue(success);
				assertNull(check);
			}
			else
			{
				assertFalse(success);
				assertNotNull(check);
			}
		}
	}
	
	@Test
	public void test_nonexistant_updates()
	{
		Employee notexist = newEmployee("nonexistant_update");
		notexist.setId(12345678);
		assertFalse(clientMap.get("kenzanu").updateEmployee(notexist));
	}
	
	@Test
	public void test_nonexistant_deletes()
	{
		Employee notexist = newEmployee("nonexistant_delete");
		notexist.setId(12345678);
		assertFalse(clientMap.get("kenzand").deleteEmployee(notexist));
	}
	
	@Test
	public void test_set_our_password()
	{
		KenzanRestClient superguy = new KenzanRestClient();
		assertTrue(superguy.login("kenzanp",  "kenzan"));

		Employee emp = this.newEmployee("ourpassword");
		assertNotEquals(-1, clientMap.get("kenzana").addEmployee(emp));
		KenzanRestClient newguy = new KenzanRestClient();
		assertTrue(superguy.setPassword(emp.getUsername(),  "hello_world_new_password"));
		assertTrue(newguy.login(emp.getUsername(), "hello_world_new_password"));
		assertTrue(newguy.setPassword("changed_password"));
		assertFalse(newguy.login(emp.getUsername(),  "hello_world_new_password"));
		assertTrue(newguy.login(emp.getUsername(), "changed_password"));
	}
	
	@Test
	public void test_fail_set_other_password()
	{
		Employee emp = this.newEmployee("failotherpw");
		assertNotEquals(-1, clientMap.get("kenzana").addEmployee(emp));
		KenzanRestClient newguy = new KenzanRestClient();
		assertFalse(clientMap.get("kenzana").setPassword(emp.getUsername(),  "hello_world_new_password"));
		assertFalse(newguy.login(emp.getUsername(),  "hello_world_new_password"));
	}
	
	@Test
	public void test_succeed_set_other_password()
	{
		KenzanRestClient superguy = new KenzanRestClient();
		assertTrue(superguy.login("kenzanp",  "kenzan"));

		Employee emp = this.newEmployee("succeeedotherpw");
		assertNotEquals(-1, clientMap.get("kenzana").addEmployee(emp));
		KenzanRestClient newguy = new KenzanRestClient();
		assertTrue(superguy.setPassword(emp.getUsername(),  "ab_123_cde"));
		assertTrue(newguy.login(emp.getUsername(),  "ab_123_cde"));
	}
}
