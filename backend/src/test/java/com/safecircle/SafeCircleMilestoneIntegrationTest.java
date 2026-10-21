package com.safecircle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safecircle.dto.LocationSubmissionRequest;
import com.safecircle.dto.RegisterRequest;
import com.safecircle.dto.SharingRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SafeCircleMilestoneIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    private static String tokenUserA;
    private static Long userIdA;

    private static String tokenUserB;
    private static Long userIdB;

    private static Long permissionId;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    @Order(1)
    @DisplayName("Milestone 1.1: Register User A and User B")
    void testRegisterUsers() throws Exception {
        // Register User A
        RegisterRequest userARequest = new RegisterRequest("Test Alice", "+19991110001", "Password123!");
        MvcResult resA = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userARequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootA = objectMapper.readTree(resA.getResponse().getContentAsString());
        assertThat(rootA.get("success").asBoolean()).isTrue();
        tokenUserA = rootA.get("data").get("token").asText();
        userIdA = rootA.get("data").get("userId").asLong();
        assertThat(tokenUserA).isNotBlank();

        // Register User B
        RegisterRequest userBRequest = new RegisterRequest("Test Bob", "+19991110002", "Password123!");
        MvcResult resB = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userBRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootB = objectMapper.readTree(resB.getResponse().getContentAsString());
        assertThat(rootB.get("success").asBoolean()).isTrue();
        tokenUserB = rootB.get("data").get("token").asText();
        userIdB = rootB.get("data").get("userId").asLong();
        assertThat(tokenUserB).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("Milestone 1.2: User A requests access to User B's location via phone number")
    void testUserARequestsLocation() throws Exception {
        SharingRequestDto requestDto = new SharingRequestDto("+19991110002", 60);

        MvcResult result = mockMvc.perform(post("/api/sharing-requests")
                .header("Authorization", "Bearer " + tokenUserA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("data").get("status").asText()).isEqualTo("PENDING");
        assertThat(root.get("data").get("active").asBoolean()).isFalse();

        permissionId = root.get("data").get("permissionId").asLong();
        assertThat(permissionId).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("Milestone 1.3: User A CANNOT view User B's location while request is PENDING (Consent Enforced)")
    void testAccessDeniedWhilePending() throws Exception {
        mockMvc.perform(get("/api/contacts/" + userIdB + "/location")
                .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DisplayName("Milestone 1.4: User B approves the sharing request")
    void testUserBApprovesRequest() throws Exception {
        MvcResult result = mockMvc.perform(patch("/api/sharing-requests/" + permissionId + "/approve")
                .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("data").get("status").asText()).isEqualTo("ACTIVE");
        assertThat(root.get("data").get("active").asBoolean()).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Milestone 1.5: User B transmits live GPS coordinates")
    void testUserBSubmitsGpsLocation() throws Exception {
        LocationSubmissionRequest loc = new LocationSubmissionRequest(37.774929, -122.419416, 4.2);

        MvcResult result = mockMvc.perform(post("/api/locations")
                .header("Authorization", "Bearer " + tokenUserB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loc)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("data").get("latitude").asDouble()).isEqualTo(37.774929);
        assertThat(root.get("data").get("longitude").asDouble()).isEqualTo(-122.419416);
    }

    @Test
    @Order(6)
    @DisplayName("Milestone 1.6: User A retrieves User B's live GPS coordinates while sharing is ACTIVE")
    void testUserARetrievesLocationSuccessfully() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/contacts/" + userIdB + "/location")
                .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("data").get("latitude").asDouble()).isEqualTo(37.774929);
        assertThat(root.get("data").get("longitude").asDouble()).isEqualTo(-122.419416);
        assertThat(root.get("data").get("accuracyM").asDouble()).isEqualTo(4.2);
    }

    @Test
    @Order(7)
    @DisplayName("Milestone 1.7: User B revokes permission and stops sharing")
    void testUserBRevokesPermission() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/sharing-permissions/" + permissionId)
                .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("data").get("status").asText()).isEqualTo("REVOKED");
        assertThat(root.get("data").get("active").asBoolean()).isFalse();
    }

    @Test
    @Order(8)
    @DisplayName("Milestone 1.8: User A CAN NO LONGER retrieve User B's location after revocation (403 Forbidden)")
    void testUserAAccessDeniedAfterRevocation() throws Exception {
        mockMvc.perform(get("/api/contacts/" + userIdB + "/location")
                .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    @DisplayName("Milestone 1.9: User B sends new GPS updates; User A remains blocked")
    void testUserAStillBlockedAfterNewUpdates() throws Exception {
        LocationSubmissionRequest newLoc = new LocationSubmissionRequest(37.780000, -122.420000, 3.5);
        mockMvc.perform(post("/api/locations")
                .header("Authorization", "Bearer " + tokenUserB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLoc)))
                .andExpect(status().isOk());

        // User A must still be strictly blocked
        mockMvc.perform(get("/api/contacts/" + userIdB + "/location")
                .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isForbidden());
    }
}
