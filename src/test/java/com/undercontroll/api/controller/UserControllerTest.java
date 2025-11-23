package com.undercontroll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.api.config.SecurityConfig;
import com.undercontroll.api.controller.impl.UserController;
import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.User;
import com.undercontroll.api.model.enums.UserType;
import com.undercontroll.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private Jwt createJwtToken(String userType, String email) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("userType", userType)
                .claim("sub", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Jwt createAdminJwtToken() {
        return createJwtToken("ADMINISTRATOR", "admin@example.com");
    }

    private Jwt createCustomerJwtToken() {
        return createJwtToken("CUSTOMER", "customer@example.com");
    }

    @Test
    @DisplayName("POST /v1/api/users - Should create user and return 201")
    void shouldCreateUserSuccessfully() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "John", "john@example.com", "11999999999", "Doe", "password123",
                "Street 123", "12345678900", null, UserType.CUSTOMER,
                false, false, true, "12345-678"
        );

        CreateUserResponse response = new CreateUserResponse(
                "John", "john@example.com", "Doe", "Street 123",
                "12345678900", "12345-678", "11999999999", null, UserType.CUSTOMER
        );

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.userType").value("CUSTOMER"));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/users/auth - Should authenticate user and return 200 with token")
    void shouldAuthenticateUserSuccessfully() throws Exception {
        AuthUserRequest request = new AuthUserRequest("john@example.com", "password123");

        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        AuthUserResponse response = new AuthUserResponse("jwt-token-here", userDto);

        when(userService.authUser(any(AuthUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/users/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));

        verify(userService, times(1)).authUser(any(AuthUserRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/users/auth/google - Should authenticate with Google and return 200")
    void shouldAuthenticateWithGoogleSuccessfully() throws Exception {
        AuthGoogleRequest request = new AuthGoogleRequest("google-token", "john@example.com");

        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        AuthUserResponse response = new AuthUserResponse("jwt-token-here", userDto);

        when(userService.authUserByGoogle(any(AuthGoogleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/users/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));

        verify(userService, times(1)).authUserByGoogle(any(AuthGoogleRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/api/users/{userId} - CUSTOMER should update user and return 200")
    void customerShouldUpdateUserSuccessfully() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "John Updated", "Doe", null, "New Address", null,
                "11999999999", null, "12345-678", false, false, true, UserType.CUSTOMER
        );

        doNothing().when(userService).updateUser(any(UpdateUserRequest.class), eq(1));

        mockMvc.perform(put("/v1/api/users/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(any(UpdateUserRequest.class), eq(1));
    }

    @Test
    @DisplayName("GET /v1/api/users - ADMINISTRATOR should get all users and return 200")
    void administratorShouldGetAllUsersSuccessfully() throws Exception {
        UserDto user1 = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        UserDto user2 = new UserDto(2, "Jane", "jane@example.com", "Doe",
                "Street 456", "98765432100", "54321-987", "11988888888",
                null, false, false, true, UserType.ADMINISTRATOR);

        when(userService.getUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/v1/api/users")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService, times(1)).getUsers();
    }

    @Test
    @DisplayName("GET /v1/api/users - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToGetAllUsers() throws Exception {
        mockMvc.perform(get("/v1/api/users")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
                .andExpect(status().isForbidden());

        verify(userService, never()).getUsers();
    }

    @Test
    @DisplayName("GET /v1/api/users/customers - ADMINISTRATOR should get customers and return 200")
    void administratorShouldGetCustomersSuccessfully() throws Exception {
        UserDto customer = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        when(userService.getCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/v1/api/users/customers")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userType").value("CUSTOMER"));

        verify(userService, times(1)).getCustomers();
    }

    @Test
    @DisplayName("GET /v1/api/users/customers - Should return 204 when no customers found")
    void shouldReturn204WhenNoCustomersFound() throws Exception {
        when(userService.getCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/users/customers")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getCustomers();
    }

    @Test
    @DisplayName("GET /v1/api/users/customers/{customerId} - Should get customer by id and return 200")
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        UserDto customer = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        when(userService.getCustomersById(1)).thenReturn(customer);

        mockMvc.perform(get("/v1/api/users/customers/1")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));

        verify(userService, times(1)).getCustomersById(1);
    }

    @Test
    @DisplayName("GET /v1/api/users/{userId} - Should get user by id and return 200")
    void shouldGetUserByIdSuccessfully() throws Exception {
        User user = User.builder()
                .id(1)
                .name("John")
                .email("john@example.com")
                .lastName("Doe")
                .userType(UserType.CUSTOMER)
                .build();

        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/v1/api/users/1")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    @DisplayName("DELETE /v1/api/users/{userId} - ADMINISTRATOR should delete user and return 200")
    void administratorShouldDeleteUserSuccessfully() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/v1/api/users/1")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    @DisplayName("DELETE /v1/api/users/{userId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToDeleteUser() throws Exception {
        mockMvc.perform(delete("/v1/api/users/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyInt());
    }
}
