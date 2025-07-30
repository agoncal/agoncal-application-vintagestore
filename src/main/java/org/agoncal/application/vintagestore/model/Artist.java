package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.Period;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@MappedSuperclass
public class Artist extends PanacheEntity {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 50, name = "first_name", nullable = false)
  @NotNull
  @Size(min = 2, max = 50)
  public String firstName;

  @Column(length = 50, name = "last_name", nullable = false)
  @NotNull
  @Size(min = 2, max = 50)
  public String lastName;

  @Column(length = 5000)
  @Size(max = 5000)
  public String bio;

  @Column(name = "date_of_birth")
  @Past
  public LocalDate dateOfBirth;

  @Transient
  public Integer age;

  // ======================================
  // =         Lifecycle methods          =
  // ======================================

  @PostLoad
  @PostPersist
  @PostUpdate
  private void calculateAge() {
    if (dateOfBirth == null) {
      age = null;
      return;
    }

    age = Period.between(dateOfBirth, LocalDate.now()).getYears();
  }
}
