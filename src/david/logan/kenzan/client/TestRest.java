package david.logan.kenzan.client;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import david.logan.kenzan.db.Employee;
import david.logan.kenzan.db.Status;

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
	public void test_duplicate_fails_and_successes()
	{
		Employee e = newEmployee("tdfas");
		// Should add
		e.setId(clientMap.get("kenzana").addEmployee(e));
		assertNotEquals(-1, e.getId());
		// Should be duplicate, because employee is active
		assertEquals(-1, clientMap.get("kenzana").addEmployee(e));

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
			//TODO: I am having troubles getting Jackson to deserialize to the correct time zone,
			// which is causing these two asserts to fail. Fix this.
			//assertEquals(0, employee.getDateOfBirth().compareTo(check.getDateOfBirth()));
		    //assertEquals(employee.getDateOfBirth(), check.getDateOfBirth());
			//assertEquals(0, employee.getDateOfEmployment().compareTo(check.getDateOfEmployment()));
			//assertEquals(employee.getDateOfEmployment(), check.getDateOfEmployment());
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
}
