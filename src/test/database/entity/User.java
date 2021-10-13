package test.database.entity;

import java.util.Objects;

import static java.util.Objects.*;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private String pass;
    private String location;
    private String gender;
    private Phone phone;

    public User(int id, String firstName, String lastName, String userName, String pass, String location,
                String gender, Phone phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.pass = pass;
        this.location = location;
        this.gender = gender;
        this.phone = phone;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() == user.getId() &&
               Objects.equals(getFirstName(), user.getFirstName()) &&
               Objects.equals(getLastName(), user.getLastName()) &&
               Objects.equals(getUserName(), user.getUserName()) &&
               Objects.equals(getPass(), user.getPass()) &&
               Objects.equals(getLocation(), user.getLocation()) &&
               Objects.equals(getGender(), user.getGender()) &&
               Objects.equals(getPhone(), user.getPhone());
    }

    @Override
    public int hashCode() {
        return hash(getId(), getFirstName(), getLastName(), getUserName(), getPass(), getLocation(),
                getGender(), getPhone());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", userName='" + userName + '\'' +
               ", pass='" + pass + '\'' +
               ", location='" + location + '\'' +
               ", gender='" + gender + '\'' +
               ", phone=" + phone +
               '}';
    }
}
