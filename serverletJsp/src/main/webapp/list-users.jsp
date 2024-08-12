<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>User List</title>
</head>
<body>
<h1>User List</h1>
<a href="add-user.jsp">Add New User</a>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Actions</th>
    </tr>
    <%
        List<User> users = (List<User>) request.getAttribute("users");
        if (users != null) {
            for (User user : users) {
    %>
    <tr>
        <td><%= user.getId() %></td>
        <td><%= user.getName() %></td>
        <td><%= user.getEmail() %></td>
        <td>
            <a href="users?action=edit&id=<%= user.getId() %>">Edit</a>
            <a href="users?action=delete&id=<%= user.getId() %>" onclick="return confirm('Are you sure you want to delete this user?');">Delete</a>

        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="4">No users found.</td>
    </tr>
    <% } %>
</table>
</body>
</html>
