<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit User</title>
</head>
<body>
<h1>Edit User</h1>
<form action="users" method="post">
    <input type="hidden" name="action" value="update"/>
    <input type="hidden" name="id" value="<%= ((User) request.getAttribute("user")).getId() %>"/>
    Name: <input type="text" name="name" value="<%= ((User) request.getAttribute("user")).getName() %>" required/><br/>
    Email: <input type="email" name="email" value="<%= ((User) request.getAttribute("user")).getEmail() %>" required/><br/>
    <input type="submit" value="Update User"/>
</form>
<a href="users">Back to User List</a>
</body>
</html>
