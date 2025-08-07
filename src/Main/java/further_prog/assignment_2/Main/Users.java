package further_prog.assignment_2.Main;

import java.io.Serializable;

public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String password;
    private String type;
    private String role;

    public Users(String name, String password, String type) {
        this.name = name;
        this.password = password;
        this.type = type;
        this.role = "USER"; // Default role
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }


    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}