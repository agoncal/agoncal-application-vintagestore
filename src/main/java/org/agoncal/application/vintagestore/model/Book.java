package org.agoncal.application.vintagestore.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
@DiscriminatorValue("B")
public class Book extends Item {

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 50)
  public String isbn;

  @Column(name = "nb_of_pages")
  @Min(1)
  public Integer nbOfPage;

  @Column(name = "publication_date")
  public LocalDate publicationDate;

  @Enumerated
  public Language language;

  @ManyToOne
  public Publisher publisher;

  @ManyToOne
  public Category category;

  @OneToMany
  @JoinTable(
    name = "book_author",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "author_id")
  )
  public Set<Author> authors = new HashSet<>();
}
