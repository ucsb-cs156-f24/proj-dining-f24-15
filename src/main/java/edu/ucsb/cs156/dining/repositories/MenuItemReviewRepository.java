package edu.ucsb.cs156.dining.repositories;

import edu.ucsb.cs156.dining.entities.MenuItemReview;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/*
This is a repository file for MenuItemReview
 */

 @Repository
 public interface MenuItemReviewRepository extends CrudRepository<MenuItemReview, Long> {
    List<MenuItemReview> findByStudentUserId(long studentUserId);
 }