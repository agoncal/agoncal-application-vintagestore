package org.agoncal.application.vintagestore.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
@DiscriminatorValue("C")
public class CD extends Item {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(name = "nb_of_discs")
  public Integer nbOfDiscs;

  @ManyToOne
  public Label label;

  @ManyToMany
  public Set<Musician> musicians = new HashSet<>();

  @ManyToOne
  public Genre genre;
}
