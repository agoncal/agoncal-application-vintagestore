package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
@Table(name = "VINTAGESTORE_USER")
public class User extends PanacheEntity {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 30)
  @NotNull
  @Size(max = 30)
  public String login;

  @Column(length = 30)
  @NotNull
  @Size(max = 30)
  public String password;

  @Column(length = 50, name = "first_name", nullable = false)
  @NotNull
  @Size(min = 2, max = 50)
  public String firstName;

  @Column(length = 50, name = "last_name", nullable = false)
  @NotNull
  @Size(min = 2, max = 50)
  public String lastName;

  @Email
  public String email;

  public UserRole role;

  @Override
  public String toString() {
    return "User{" +
      "login='" + login + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", email='" + email + '\'' +
      ", role=" + role +
      ", id=" + id +
      '}';
  }
}
