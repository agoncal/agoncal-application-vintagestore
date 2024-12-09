package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Antonio Goncalves
 * http://www.antoniogoncalves.org
 * --
 */

@Entity
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("I")
@NamedQueries({
  @NamedQuery(name = Item.FIND_TOP_RATED, query = "SELECT i FROM Item i WHERE i.id in :ids"),
  @NamedQuery(name = Item.SEARCH, query = "SELECT i FROM Item i WHERE UPPER(i.title) LIKE :keyword OR UPPER(i.description) LIKE :keyword ORDER BY i.title")

})
public class Item extends PanacheEntity {

  // ======================================
  // =             Constants              =
  // ======================================

  public static final String FIND_TOP_RATED = "Item.findTopRated";
  public static final String SEARCH = "Item.search";

  // ======================================
  // =             Attributes             =
  // ======================================

  @Column(length = 200)
  @NotNull
  @Size(min = 1, max = 200)
  public String title;

  @Column(length = 10000)
  @Size(min = 1, max = 10000)
  public String description;

  @Column(name = "unit_cost")
  @Min(1)
  public Float unitCost;

  public Integer rank;

  @Column(name = "small_image_url")
  public String smallImageURL;

  @Column(name = "medium_image_url")
  public String mediumImageURL;

  public static long findLikeTitle(String title) {
    return count("UPPER(title) like ?1", "%" + title.toUpperCase() + "%");
  }
}
