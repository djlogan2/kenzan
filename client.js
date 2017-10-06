var Client = require('node-rest-client').Client;
 
// direct way 
var client = new Client();
var authorization;
var current_data;

var login_args = {
    data: {username: "kenzan", password: "kenzan" },
    headers: { "Content-Type": "application/json" }
};

function test8()
{
}

function test7() {
  console.log("test7");
  client.post("http://localhost:8080/Kenzan/rest/get_all", {headers: {"Authorization": authorization, "Content-Type": "application/json" } },
      function (data, response) {
        if(data.length + 1 != current_data.length)
          console.log("test7 error 1");
        current_data = data;
        test8();
      });
}

function test6() {
  console.log("test6");
  record = current_data[2];
  record.bStatus = 'INACTIVE';
  client.post("http://localhost:8080/Kenzan/rest/update_emp", {data: record, headers: {"Authorization": authorization, "Content-Type": "application/json" } },
      function (data, response) {
        if(data.error != "ok") {
          console.log("test6 error 1");
          console.log(data);
        }
        test7();
      });
}

function test5() {
  console.log("test5");
  client.post("http://localhost:8080/Kenzan/rest/get_all", {headers: {"Authorization": authorization, "Content-Type": "application/json" } },
      function (data, response) {
        if(data.error != null)
          console.log("test5 error 1");
        data.forEach(function(record) {
          if(record.bStatus != 'ACTIVE')
            console.log("test5 error 2");
        });
        current_data = data;
        test6();
      });
}

function test4() {
  console.log("test4");
  client.post("http://localhost:8080/Kenzan/rest/login", {data: {username: "kenzan", password: "kenzan"}, headers: {"Content-Type": "application/json" } },
      function (data, response) {
        if(data.jwt == null) {
          console.log("We should have gotten a valid jwt");
          process.exit();
        }
        if(data.error != null) {
          console.log("Invalid error message: " + data.error);
          process.exit();
        };
        authorization = data.jwt;
        test5();
      });
}

function test3() {
  console.log("test3");
  client.post("http://localhost:8080/Kenzan/rest/login", {data: {username: "kenzan", password: "kenzan"}, headers: {"Content-Type": "application/json" } },
      function (data, response) {
        if(data.jwt == null)
          console.log("We should have gotten a valid jwt");
        if(data.error != null)
          console.log("Invalid error message: " + data.error);
        console.log(data.jwt);
        test4();
      });
}

function test2() {
  console.log("test2");
  client.post("http://localhost:8080/Kenzan/rest/login", {data: {username: "kenzan", password: "invalid"}, headers: {"Content-Type": "application/json" } },
      function (data, response) {
        if(data.jwt != null)
          console.log("jwt not null when it should be");
        if(data.error != 'Unable to validate user/password combination')
          console.log("Invalid error message");
        test3();
      });
}

function test1() { 
  console.log("test1");
  client.post("http://localhost:8080/Kenzan/rest/login", {data: {username: "invalid", password: "invalid"}, headers: {"Content-Type": "application/json" } },
      function (data, response) {
        if(data.jwt != null)
          console.log("jwt not null when it should be");
        if(data.error != 'Unable to validate user/password combination')
          console.log("Invalid error message");
        test2();
      });
};

test1(); 
var record = {
	firstName: "John",
	lastName: "Doe",
	dateOfBirth: "1990-12-25",
	dateOfEmployment: "2000-01-01",
	bStatus: "ACTIVE"
};
 
var args = {
    data: record, // data passed to REST method (only useful in POST, PUT or PATCH methods) 
    headers: { "Authorization": "abc", "Content-Type": "application/json" } // request headers 
};
 
