var http = require('http');

http.get("http://localhost:3000/version", function(res) {
    window.location="http://localhost:3000"
}).on('error', function(e) {
    alert("Please start the server.");
});
