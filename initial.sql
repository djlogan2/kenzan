DROP TRIGGER IF EXISTS VerifyEmployeeInsert;
DROP TRIGGER IF EXISTS VerifyEmployeeUpdate;
DROP TABLE IF EXISTS employee_role_join;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS employeerole;

CREATE TABLE `employee` (
  `id` int(11) NOT NULL UNIQUE AUTO_INCREMENT,
  `bStatus` int(11) NOT NULL,
  `dateOfBirth` datetime NOT NULL,
  `dateOfEmployment` datetime DEFAULT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `middleInitial` char(1) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DELIMITER $$

CREATE TRIGGER VerifyEmployeeInsert BEFORE INSERT ON employee
  FOR EACH ROW
    BEGIN
      IF (SELECT COUNT(1) FROM employee WHERE employee.username = NEW.username AND employee.bStatus = 0) > 0 THEN
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Unable to insert duplicate username';
      END IF;
    END;
$$

CREATE TRIGGER VerifyEmployeeUpdate BEFORE UPDATE ON employee
  FOR EACH ROW
    BEGIN
      IF NEW.bStatus = 0 THEN
        BEGIN
          IF (SELECT COUNT(1) FROM employee WHERE NEW.username = employee.username AND employee.id <> NEW.id and employee.bStatus = 0) > 0 THEN
            SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Unable to insert duplicate username';
          END IF;
        END;
      END IF;
    END;
$$

DELIMITER ;

CREATE TABLE `employeerole` (
  `id` int(11) NOT NULL UNIQUE AUTO_INCREMENT,
  `role` varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
);

CREATE TABLE `employee_role_join` (
  `employee_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`employee_id`,`role_id`),
  FOREIGN KEY ( employee_id ) REFERENCES employee ( id ) ON DELETE CASCADE,
  FOREIGN KEY ( role_id ) REFERENCES employeerole ( id ) ON DELETE CASCADE
);

insert into employeerole (role) values ('ROLE_ADD_EMP');
insert into employeerole (role) values ('ROLE_UPDATE_EMP');
insert into employeerole (role) values ('ROLE_DELETE_EMP');
insert into employeerole (role) values ('ROLE_SET_PASSWORD');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-11-26', '2001-01-01', 'Kenzan', 'Test', 'A', 'kenzan');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-11-27', '2002-02-02', 'Kenzan', 'Test A', 'B', 'kenzana');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-11-28', '2003-03-03', 'Kenzan', 'Test AD', 'C', 'kenzanad');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-11-29', '2004-04-04', 'Kenzan', 'Test AU', 'D', 'kenzanau');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-11-30', '2005-05-05', 'Kenzan', 'Test ADU', 'E', 'kenzanadu');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-12-01', '2006-06-06', 'Kenzan', 'Test D', 'F', 'kenzand');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-12-02', '2007-07-07', 'Kenzan', 'Test DU', 'G', 'kenzandu');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-12-03', '2008-08-08', 'Kenzan', 'Test U', 'H', 'kenzanu');
insert into employee (bStatus, dateOfBirth, dateOfEmployment, firstName, lastName, middleInitial, username) values (0, '1968-12-04', '2008-08-09', 'Kenzan', 'Test P', 'H', 'kenzanp');
update employee set password='$2a$10$i51OFodMCqcVjvDyQUt8IeYhtuMH7J6JqUXKwWWPCP00DcgHnIscG'; /* password='kenzan' */
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzana'), (select id from employeerole where role='ROLE_ADD_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanad'), (select id from employeerole where role='ROLE_ADD_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanau'), (select id from employeerole where role='ROLE_ADD_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanadu'), (select id from employeerole where role='ROLE_ADD_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzand'), (select id from employeerole where role='ROLE_DELETE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzandu'), (select id from employeerole where role='ROLE_DELETE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanad'), (select id from employeerole where role='ROLE_DELETE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanadu'), (select id from employeerole where role='ROLE_DELETE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanadu'), (select id from employeerole where role='ROLE_UPDATE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzandu'), (select id from employeerole where role='ROLE_UPDATE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanau'), (select id from employeerole where role='ROLE_UPDATE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanu'), (select id from employeerole where role='ROLE_UPDATE_EMP'));
insert into employee_role_join (employee_id, role_id) values ((select id from employee where username='kenzanp'), (select id from employeerole where role='ROLE_SET_PASSWORD'));