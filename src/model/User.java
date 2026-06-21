package model;

/**
 * Represents a system user. Role determines which dashboard/permissions
 * the user receives after login (Admin / Owner / Customer).
 */
public class User {

    public enum Role {
        ADMIN, OWNER, CUSTOMER
    }

    private String userId;
    private String name;
    private String phone;
    private Role role;

    public User(String userId, String name, String phone, Role role) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
