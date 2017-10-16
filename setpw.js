var Client = require('node-rest-client').Client;
 
// direct way 
var client = new Client();
var authorization;
var current_data;

var login_args = {
    data: {username: "kenzan", password: "kenzan" },
    headers: { "Content-Type": "application/json" }
};

var username        = process.argv[2];
var password        = process.argv[3];
var username_to_set = process.argv[4];
var password_to_set = process.argv[5];


function setPassword(jwt) {
  client.post("http://localhost:8080/Kenzan/rest/set_password", {data: {username: username_to_set, password: password_to_set}, headers: {"Authorization": jwt, "Content-Type": "application/json" } },
      function (data, response) {
        if(data.error != "ok")
          console.log("Invalid error message: " + data.error);
        else
          console.log("Password set");
      });
}

client.post("http://localhost:8080/Kenzan/rest/login", {data: {username: username, password: password}, headers: {"Content-Type": "application/json" } },
    function (data, response) {
      if(data.jwt == null)
        console.log("Unable to obtain JSON web token");
      else if(data.error != null)
        console.log("Invalid error message: " + data.error);
      else
        setPassword(data.jwt);
    });
