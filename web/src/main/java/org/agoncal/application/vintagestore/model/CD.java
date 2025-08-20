package org.agoncal.application.vintagestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  @ManyToMany
  @JoinTable(
    name = "cd_musician",
    joinColumns = @JoinColumn(name = "cd_id"),
    inverseJoinColumns = @JoinColumn(name = "musician_id")
  )
  public Set<Musician> musicians = new HashSet<>();

  @ManyToOne
  public Genre genre;
}
