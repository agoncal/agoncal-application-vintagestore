package org.agoncal.application.vintagestore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
public class Musician extends Artist {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(name = "preferred_instrument")
  public String preferredInstrument;

  @ManyToMany
  public Set<CD> cds = new HashSet<>();
}
