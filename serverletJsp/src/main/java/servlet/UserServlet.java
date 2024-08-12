package servlet;

import model.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:mysql://localhost:3306/user_db";
    private static final String USER = "root";
    private static final String PASSWORD = "oms123"; // Update with your database password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            handleEditRequest(request, response);
        } else if ("delete".equals(action)) {
            deleteUser(request, response);
        } else {
            listUsers(request, response);
        }
    }

    private void handleEditRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("user", new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email")
                    ));
                    request.getRequestDispatcher("/edit-user.jsp").forward(request, response);
                } else {
                    response.sendRedirect("users");
                }
            }
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving user", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add":
                addUser(request, response);
                break;
            case "update":
                updateUser(request, response);
                break;
            case "delete":
                // This case should not be reached since deletion is handled in doGet
                response.sendRedirect("users");
                break;
            default:
                response.sendRedirect("users");
                break;
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
            request.setAttribute("error", "Name and email are required.");
            request.getRequestDispatcher("/add-user.jsp").forward(request, response);
            return;
        }

        String query = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            response.sendRedirect("users");
        } catch (SQLException e) {
            handleError(request, response, "Error adding user", e);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
            request.setAttribute("error", "Name and email are required.");
            request.getRequestDispatcher("/edit-user.jsp").forward(request, response);
            return;
        }

        String query = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.sendRedirect("users");
            } else {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            handleError(request, response, "Error updating user", e);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            request.setAttribute("error", "Invalid user ID.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int id = Integer.parseInt(idParam);
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.sendRedirect("users");
            } else {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            handleError(request, response, "Error deleting user", e);
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = "SELECT * FROM users";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
            request.setAttribute("users", users);
            request.getRequestDispatcher("/list-users.jsp").forward(request, response);
        } catch (SQLException e) {
            handleError(request, response, "Error retrieving users", e);
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message, Exception e) throws ServletException, IOException {
        e.printStackTrace(); // Log the exception stack trace
        request.setAttribute("error", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
}
