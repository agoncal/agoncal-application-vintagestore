package org.agoncal.application.vintagestore;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
public class Label extends PanacheEntity {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 30)
  @NotNull
  @Size(max = 30)
  public String name;
}
