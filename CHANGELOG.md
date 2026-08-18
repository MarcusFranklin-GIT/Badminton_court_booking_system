# 📝 Changelog

## Version 0.2.0 - Authentication & Authorization (2026-08-18)

### ✨ New Features

#### 🔐 JWT Authentication
- Implemented token-based authentication using JSON Web Tokens (JWT)
- Tokens expire after 24 hours
- Secure token generation with HMAC SHA-256

#### 👥 User Management
- User registration endpoint (`/api/auth/register`)
- User login endpoint (`/api/auth/login`)
- Admin registration endpoint (`/api/auth/register-admin`)
- User model with email, name, phone, and password
- Passwords hashed using BCrypt

#### 🛡️ Role-Based Access Control
- **Two roles implemented:**
  - `CUSTOMER` - Can view courts and manage their bookings
  - `ADMIN` - Can manage courts + all customer permissions

- **Access control rules:**
  - Public: View courts (GET), Authentication endpoints
  - Authenticated: All booking operations
  - Admin only: Create, update, delete courts

#### 🔒 Security Features
- Spring Security integration
- JWT authentication filter
- Custom UserDetailsService
- Password encryption with BCrypt
- Stateless session management
- CORS and CSRF configuration

### 📦 New Dependencies
- `spring-boot-starter-security` - Spring Security framework
- `jjwt-api` 0.11.5 - JWT API
- `jjwt-impl` 0.11.5 - JWT implementation
- `jjwt-jackson` 0.11.5 - JWT JSON processing

### 📁 New Files Created

**Models:**
- `User.java` - User entity with roles

**DTOs:**
- `LoginRequest.java` - Login credentials
- `RegisterRequest.java` - Registration data
- `AuthResponse.java` - Authentication response with token

**Security:**
- `JwtUtil.java` - JWT token generation and validation
- `CustomUserDetailsService.java` - Spring Security UserDetailsService
- `JwtAuthenticationFilter.java` - Request filter for JWT validation
- `SecurityConfig.java` - Spring Security configuration

**Services:**
- `AuthService.java` - Authentication business logic

**Controllers:**
- `AuthController.java` - Authentication endpoints

**Repositories:**
- `UserRepository.java` - User data access

**Documentation:**
- `AUTHENTICATION_GUIDE.md` - Complete authentication guide
- `CHANGELOG.md` - This file

### 🔄 Modified Files
- `pom.xml` - Added security dependencies
- `README.md` - Updated with authentication features
- `CourtController.java` - No changes (protected by SecurityConfig)
- `BookingController.java` - No changes (protected by SecurityConfig)

### 🧪 Testing
- ✅ Customer registration works
- ✅ Admin registration works
- ✅ Login returns valid JWT token
- ✅ Admin can create courts
- ✅ Customer cannot create courts (403 Forbidden)
- ✅ Both can create bookings
- ✅ Public endpoints work without authentication

### 📊 Test Results
```
✓ Admin registered with roles: [ADMIN, CUSTOMER]
✓ Customer registered with roles: [CUSTOMER]
✓ Admin successfully created court
✓ Customer blocked from creating court (403)
✓ Customer successfully created booking
```

### 🔜 Future Improvements
- [ ] Token refresh mechanism
- [ ] Email verification
- [ ] Password reset
- [ ] Change password endpoint
- [ ] User profile management
- [ ] OAuth2 integration
- [ ] Two-factor authentication
- [ ] Account management for admins

---

## Version 0.1.0 - Initial Release (2026-08-18)

### ✨ Features
- Court management (CRUD operations)
- Booking system with time slot management
- Conflict detection for overlapping bookings
- Automatic price calculation
- Booking status tracking
- MongoDB integration
- REST API

### 📦 Dependencies
- Spring Boot 3.2.0
- Spring Data MongoDB
- Lombok
- Validation

### 📁 Files Created
- Models: `Court.java`, `Booking.java`
- Repositories: `CourtRepository.java`, `BookingRepository.java`
- Services: `CourtService.java`, `BookingService.java`
- Controllers: `CourtController.java`, `BookingController.java`
- Documentation: `README.md`, `POSTMAN_TESTING.md`

---

**Built with ❤️ using Vibe Coding principles**
