package edu.ucsb.cs156.dining.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import edu.ucsb.cs156.dining.models.Meal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RestClientTest(UCSBAPIMenuService.class)
public class UCSBAPIMenuServiceTest {

    @Autowired
    private UCSBAPIMenuService menuService;

    @Autowired
    private MockRestServiceServer mockServer;

    @MockBean
    private edu.ucsb.cs156.dining.services.wiremock.WiremockService wiremockService;

    @Value("${app.ucsb.api.consumer_key}")
    private String apiKey;

    @Test
    public void testGetMeals_Success() throws Exception {
        // Arrange: Mock successful API response
        String mockResponse = """
            [
                { "name": "Breakfast", "code": "breakfast" },
                { "name": "Lunch", "code": "lunch" },
                { "name": "Dinner", "code": "dinner" }
            ]
        """;

        String expectedUrl = "https://api.ucsb.edu/dining/menu/v1/2024-11-20/de-la-guerra";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(header("ucsb-api-key", apiKey))
                .andExpect(header("accept", "application/json"))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // Act
        List<Meal> result = menuService.getMeals("2024-11-20", "de-la-guerra");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        Meal breakfast = result.get(0);
        assertEquals("Breakfast", breakfast.getName());
        assertEquals("breakfast", breakfast.getCode());

        Meal lunch = result.get(1);
        assertEquals("Lunch", lunch.getName());
        assertEquals("lunch", lunch.getCode());

        Meal dinner = result.get(2);
        assertEquals("Dinner", dinner.getName());
        assertEquals("dinner", dinner.getCode());
    }

    @Test
    public void testGetMeals_NullResponse() {
        // Arrange: Mock API response with null body
        String expectedUrl = "https://api.ucsb.edu/dining/menu/v1/2024-11-20/de-la-guerra";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(header("ucsb-api-key", apiKey))
                .andRespond(withNoContent()); // Simulates a 204 No Content response

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            menuService.getMeals("2024-11-20", "de-la-guerra");
        });
        assertEquals("Failed to fetch meals data from API", exception.getMessage());
    }

    @Test
    public void testGetMeals_InvalidApiKey() {
        // Arrange: Mock API response with 401 Unauthorized
        String expectedUrl = "https://api.ucsb.edu/dining/menu/v1/2024-11-20/de-la-guerra";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(header("ucsb-api-key", apiKey))
                .andRespond(withUnauthorizedRequest());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            menuService.getMeals("2024-11-20", "de-la-guerra");
        });
        assertTrue(exception.getMessage().contains("401 Unauthorized"));
    }
}
