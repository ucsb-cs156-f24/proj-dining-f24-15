package edu.ucsb.cs156.dining.services;

import edu.ucsb.cs156.dining.models.Meal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/** Service class to interact with Meal */
@Service
@Slf4j
public class MealService {

  @Value("${app.ucsb.api.consumer_key}")
  private String apiKey;

  private final RestTemplate restTemplate;

  public MealService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  private static final String MEAL_ENDPOINT =
      "https://api.ucsb.edu/dining/menu/v1/";

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Fetches all meals served in a particular dining commons on a specific date.
   *
   * @param dateTime the date and time as a LocalDateTime
   * @param diningCommonsCode the dining commons code
   * @return a list of meals (e.g., breakfast, lunch, dinner)
   * @throws Exception if the API request fails
   */
  public List<Meal> getMeals(LocalDateTime dateTime, String diningCommonsCode) throws Exception {
    // Convert LocalDateTime to YYYY-MM-DD format
    String formattedDate = dateTime.format(DATE_FORMATTER);

    String url = String.format("%s%s/%s", MEAL_ENDPOINT, formattedDate, diningCommonsCode);

    HttpHeaders headers = new HttpHeaders();
    headers.set("ucsb-api-key", this.apiKey);
    headers.set("accept", "application/json");

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    log.info("Fetching meals for date: {}, dining commons: {}", formattedDate, diningCommonsCode);

    try {
        ResponseEntity<Meal[]> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Meal[].class);

        Meal[] mealsArray = response.getBody();
        if (mealsArray == null) {
            throw new Exception("Failed to fetch meals data from API");
        }

        return List.of(mealsArray);
    } catch (HttpClientErrorException.NotFound e) {
        log.error("API returned 404 for date: {} and dining commons: {}", formattedDate, diningCommonsCode);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meals not found for given date and dining commons");
    }
  }
}
