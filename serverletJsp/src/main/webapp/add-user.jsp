<!DOCTYPE html>
<html>
<head>
    <title>Add User</title>
</head>
<body>
<h1>Add New User</h1>
<form action="users" method="post">
    <input type="hidden" name="action" value="add"/>
    Name: <input type="text" name="name" required/><br/>
    Email: <input type="email" name="email" required/><br/>
    <input type="submit" value="Add User"/>
</form>
<a href="users">Back to User List</a>
</body>
</html>
