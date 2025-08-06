package org.agoncal.application.vintagestore.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
public class Item extends PanacheEntity {


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
    return count("UPPER(title) like ?1", "%" + title.trim().toUpperCase() + "%");
  }

  public static List<Item> findTopRated() {
    return list("rank = ?1", 5);
  }

  public static List<Item> search(String keyword) {
    return list("UPPER(title) like ?1 OR UPPER(description) like ?1 ORDER BY title", "%" + keyword.trim().toUpperCase() + "%");
  }
}
