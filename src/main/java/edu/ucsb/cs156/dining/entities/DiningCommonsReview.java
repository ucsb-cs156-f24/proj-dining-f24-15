package edu.ucsb.cs156.dining.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 
 * This is a JPA entity that represents a DiningCommonsReview
 * 
 * A DiningCommonsReview is a review for a dining commons at UCSB
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "diningcommonsreview")
public class DiningCommonsReview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private Long studentUserId;
  private Long itemId;
  private LocalDateTime itemServedDate;
  private String status;
  private Long moderatorUserId;
  private String moderatorComments;
  private LocalDateTime createdDate;
  private LocalDateTime lastEditedDate;


}