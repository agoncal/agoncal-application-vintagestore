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
public class Genre extends PanacheEntity {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 100)
  @NotNull
  @Size(max = 100)
  public String name;
}
