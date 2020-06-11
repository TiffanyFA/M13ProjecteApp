package org.insbaixcamp.projectem13.Model;

public class User {
    private String auth_id;
    private String name;
    private String phone;
    private String email;

    public User() {
    }

    public User(String auth_id, String name, String phone, String email) {
        this.auth_id = auth_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getAuth_id() {
        return auth_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
