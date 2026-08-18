# 🧠 How Batmition Works - Complete Technical Guide

A deep dive into how the badminton court booking system works from request to response.

---

## 📑 Table of Contents

1. [System Architecture Overview](#system-architecture-overview)
2. [The Authentication System](#the-authentication-system)
3. [The Authorization System](#the-authorization-system)
4. [The Booking System](#the-booking-system)
5. [Database Design & Storage](#database-design--storage)
6. [Request Flow - Step by Step](#request-flow---step-by-step)
7. [Code Walkthrough](#code-walkthrough)
8. [Security Deep Dive](#security-deep-dive)
9. [Common Scenarios Explained](#common-scenarios-explained)

---

## 🏗️ System Architecture Overview

### The Big Picture

```
┌──────────────────────────────────────────────────────────────┐
│                         CLIENT                               │
│  (Postman / Browser / Mobile App / Any HTTP Client)        │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         │ HTTP Request (JSON)
                         │ GET, POST, PUT, DELETE, PATCH
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│               SPRING BOOT APPLICATION (Port 8081)            │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │          🛡️ SECURITY LAYER (Always First!)            │ │
│  │                                                         │ │
│  │  JwtAuthenticationFilter                               │ │
│  │  ├─ Runs on EVERY request                             │ │
│  │  ├─ Extracts JWT token from header                    │ │
│  │  ├─ Validates token (signature, expiry)               │ │
│  │  └─ Sets user authentication in SecurityContext       │ │
│  │                                                         │ │
│  │  Spring Security                                       │ │
│  │  ├─ Checks if endpoint requires authentication        │ │
│  │  ├─ Checks if user has required role                  │ │
│  │  └─ Allows or denies request                          │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│                        │ (If allowed)                        │
│                        ▼                                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           📡 CONTROLLER LAYER (REST API)               │ │
│  │                                                         │ │
│  │  AuthController     - /api/auth/**                     │ │
│  │  CourtController    - /api/courts/**                   │ │
│  │  BookingController  - /api/bookings/**                 │ │
│  │                                                         │ │
│  │  Job: Convert HTTP → Java method calls                 │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│                        │                                      │
│                        ▼                                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           💼 SERVICE LAYER (Business Logic)            │ │
│  │                                                         │ │
│  │  AuthService     - Registration, Login, JWT            │ │
│  │  CourtService    - Court CRUD operations               │ │
│  │  BookingService  - Booking logic, validation, pricing  │ │
│  │                                                         │ │
│  │  Job: Apply business rules, calculate, validate        │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│                        │                                      │
│                        ▼                                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         🗄️ REPOSITORY LAYER (Data Access)              │ │
│  │                                                         │ │
│  │  UserRepository                                        │ │
│  │  CourtRepository                                       │ │
│  │  BookingRepository                                     │ │
│  │                                                         │ │
│  │  Job: Talk to database (MongoDB queries)              │ │
│  └─────────────────────┬───────────────────────────────────┘ │
└────────────────────────┼────────────────────────────────────┘
                         │
                         │ MongoDB Queries
                         │
                         ▼
              ┌─────────────────────┐
              │      MONGODB        │
              │                     │
              │  ┌─────────────┐   │
              │  │   users     │   │
              │  ├─────────────┤   │
              │  │   courts    │   │
              │  ├─────────────┤   │
              │  │   bookings  │   │
              │  └─────────────┘   │
              └─────────────────────┘
```

### Key Principles

1. **Layered Architecture** - Each layer has specific responsibilities
2. **Security First** - Every request goes through security filters
3. **Separation of Concerns** - Controllers don't talk to database directly
4. **Stateless** - No server-side sessions, JWT tokens contain everything

---

## 🔐 The Authentication System

### What is Authentication?

**Authentication = Proving who you are**

Think of it like showing your ID at airport security.

### How JWT (JSON Web Token) Works

#### Step 1: User Registers

```
User fills form:
├─ Name: "John Doe"
├─ Email: "john@example.com"
├─ Password: "mypassword123"
└─ Phone: "9876543210"
       │
       ▼
POST /api/auth/register
       │
       ▼
AuthController.register() receives request
       │
       ▼
AuthService.register() processes
       │
       ├─→ Check: Does email already exist?
       │   UserRepository.existsByEmail("john@example.com")
       │   Query MongoDB: db.users.findOne({ email: "john@example.com" })
       │   
       │   If exists → Error ❌
       │   If not exists → Continue ✅
       │
       ├─→ Create User object:
       │   User user = new User();
       │   user.setName("John Doe");
       │   user.setEmail("john@example.com");
       │   user.setPassword(???)  ← Can't store plain password!
       │
       ├─→ Hash the password (BCrypt):
       │   
       │   Input:  "mypassword123"
       │           ↓
       │   BCrypt adds random salt
       │           ↓
       │   Multiple rounds of hashing (slow on purpose!)
       │           ↓
       │   Output: "$2a$10$N9qo8uLOlO7rJo8uLO..."
       │   
       │   This hash is ONE-WAY! Cannot reverse it!
       │   Same password creates different hash each time (salt)
       │   
       │   user.setPassword("$2a$10$N9qo8uLO...")
       │
       ├─→ Set default role:
       │   user.setRoles(Set.of(Role.CUSTOMER))
       │
       ├─→ Save to database:
       │   UserRepository.save(user)
       │   MongoDB: db.users.insertOne({
       │     name: "John Doe",
       │     email: "john@example.com",
       │     password: "$2a$10$N9qo8uLO...",
       │     roles: ["CUSTOMER"],
       │     enabled: true,
       │     createdAt: "2026-08-18T10:00:00Z"
       │   })
       │
       └─→ Generate JWT Token:
           
           JwtUtil.generateToken("john@example.com")
           
           Step 1: Create payload (claims)
           {
             "sub": "john@example.com",      ← Subject (who)
             "iat": 1787030000,              ← Issued At (when)
             "exp": 1787116400               ← Expires (24h later)
           }
           
           Step 2: Create header
           {
             "alg": "HS256",                 ← Algorithm
             "typ": "JWT"                    ← Type
           }
           
           Step 3: Encode header + payload (Base64)
           eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzg3MDMwMDAwLCJleHAiOjE3ODcxMTY0MDB9
           
           Step 4: Create signature
           HMAC_SHA256(
             encodedHeader + "." + encodedPayload,
             SECRET_KEY  ← Only server knows this!
           )
           = xzP9k2L...
           
           Step 5: Combine all parts
           TOKEN = header.payload.signature
                 = eyJhbGci...eyJzdWI...xzP9k2L...
           
           Return token to user
       │
       ▼
Response to user:
{
  "token": "eyJhbGci...eyJzdWI...xzP9k2L...",
  "email": "john@example.com",
  "name": "John Doe",
  "roles": ["CUSTOMER"]
}
```

#### Step 2: User Logs In

```
User enters credentials:
├─ Email: "john@example.com"
└─ Password: "mypassword123"
       │
       ▼
POST /api/auth/login
       │
       ▼
AuthService.login()
       │
       ├─→ Spring Security AuthenticationManager validates:
       │   
       │   Step 1: Find user by email
       │   UserRepository.findByEmail("john@example.com")
       │   MongoDB: db.users.findOne({ email: "john@example.com" })
       │   
       │   Returns: User{
       │     email: "john@example.com",
       │     password: "$2a$10$N9qo8uLO...",
       │     roles: ["CUSTOMER"]
       │   }
       │   
       │   Step 2: Compare passwords
       │   BCrypt.matches(
       │     "mypassword123",              ← What user typed
       │     "$2a$10$N9qo8uLO..."         ← What's in database
       │   )
       │   
       │   BCrypt recreates hash with same salt
       │   Compares: Match? ✅ or No? ❌
       │   
       │   If no match → throw BadCredentialsException
       │   If match → Continue ✅
       │
       └─→ Generate NEW JWT token (same process as registration)
           Return token to user
       │
       ▼
Response:
{
  "token": "eyJhbGci...NEW_TOKEN...",
  "email": "john@example.com",
  "name": "John Doe",
  "roles": ["CUSTOMER"]
}
```

#### Step 3: Using the Token in Requests

```
Every subsequent request:

Client stores token (in memory, localStorage, etc.)

When making request:
GET /api/bookings/my-bookings?email=john@example.com
Header: Authorization: Bearer eyJhbGci...TOKEN...
       │
       ▼
Request enters Spring Boot
       │
       ▼
JwtAuthenticationFilter.doFilterInternal() executes FIRST
       │
       ├─→ Step 1: Extract token from header
       │   
       │   String authHeader = request.getHeader("Authorization");
       │   // authHeader = "Bearer eyJhbGci...TOKEN..."
       │   
       │   if (authHeader != null && authHeader.startsWith("Bearer ")) {
       │     jwt = authHeader.substring(7);  // Remove "Bearer "
       │     // jwt = "eyJhbGci...TOKEN..."
       │   }
       │
       ├─→ Step 2: Decode and extract email
       │   
       │   JwtUtil.extractEmail(jwt)
       │   
       │   Decode payload (Base64)
       │   Parse JSON
       │   Extract "sub" field → "john@example.com"
       │
       ├─→ Step 3: Load user from database
       │   
       │   CustomUserDetailsService.loadUserByUsername("john@example.com")
       │   
       │   UserRepository.findByEmail("john@example.com")
       │   MongoDB: db.users.findOne({ email: "john@example.com" })
       │   
       │   Convert to Spring Security UserDetails:
       │   UserDetails{
       │     username: "john@example.com",
       │     password: "$2a$10$...",
       │     authorities: ["ROLE_CUSTOMER"]  ← roles prefixed with "ROLE_"
       │   }
       │
       ├─→ Step 4: Validate token
       │   
       │   JwtUtil.validateToken(jwt, "john@example.com")
       │   
       │   Check 1: Email matches?
       │   extractEmail(jwt) == "john@example.com" ✅
       │   
       │   Check 2: Not expired?
       │   extractExpiration(jwt) > now() ✅
       │   
       │   Check 3: Signature valid?
       │   HMAC_SHA256(header + payload, SECRET_KEY) == signature ✅
       │   (If someone modified token, signature won't match!)
       │   
       │   All checks pass? ✅
       │
       └─→ Step 5: Set authentication in SecurityContext
           
           UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
             userDetails,           // Principal (who)
             null,                  // Credentials (not needed)
             userDetails.getAuthorities()  // Roles
           );
           
           SecurityContextHolder.getContext().setAuthentication(authToken);
           
           Now Spring Security knows:
           - Who is making request: john@example.com
           - What roles they have: CUSTOMER
       │
       ▼
Request continues to controller
(User is authenticated!)
```

---

## 🛡️ The Authorization System

### What is Authorization?

**Authorization = What you're allowed to do**

After proving WHO you are (authentication), now we check WHAT you can do.

### How Spring Security Checks Permissions

**Configuration in SecurityConfig.java:**

```java
http.authorizeHttpRequests(auth -> auth
    // Rule 1: Auth endpoints are public
    .requestMatchers("/api/auth/**").permitAll()
    
    // Rule 2: Viewing courts is public
    .requestMatchers(HttpMethod.GET, "/api/courts/**").permitAll()
    
    // Rule 3: Managing courts requires ADMIN
    .requestMatchers(HttpMethod.POST, "/api/courts/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/api/courts/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/courts/**").hasRole("ADMIN")
    
    // Rule 4: All booking operations require authentication
    .requestMatchers("/api/bookings/**").authenticated()
    
    // Rule 5: Everything else requires authentication
    .anyRequest().authenticated()
)
```

### Authorization Check Flow

```
Request: POST /api/courts
Header: Authorization: Bearer TOKEN
Body: { "name": "Court 1", ... }
       │
       ▼
JWT Filter validated token ✅
User: john@example.com
Roles: [ROLE_CUSTOMER]
       │
       ▼
Spring Security AuthorizationFilter checks rules:
       │
       ├─→ Find matching rule:
       │   Path: /api/courts
       │   Method: POST
       │   
       │   Matches: .requestMatchers(HttpMethod.POST, "/api/courts/**").hasRole("ADMIN")
       │   
       │   Required role: ADMIN
       │
       ├─→ Check user's roles:
       │   User has: [ROLE_CUSTOMER]
       │   Required: ROLE_ADMIN
       │   
       │   Has ROLE_ADMIN? NO ❌
       │
       └─→ Decision: DENY
           Throw AccessDeniedException
           │
           ▼
ExceptionTranslationFilter catches exception
           │
           ▼
Return HTTP 403 Forbidden to client


Now with ADMIN user:

Request: POST /api/courts
Header: Authorization: Bearer ADMIN_TOKEN
       │
       ▼
JWT Filter validated token ✅
User: admin@batmition.com
Roles: [ROLE_ADMIN, ROLE_CUSTOMER]
       │
       ▼
AuthorizationFilter checks:
       │
       ├─→ Required: ROLE_ADMIN
       ├─→ User has: [ROLE_ADMIN, ROLE_CUSTOMER]
       ├─→ Has ROLE_ADMIN? YES ✅
       │
       └─→ Decision: ALLOW
           │
           ▼
Request reaches CourtController.createCourt() ✅
```

---

## 📅 The Booking System

### The Booking Creation Flow

This is the most complex part - let's break it down completely.

```
Customer wants to book Court 1 from 10:00-12:00 on Aug 25, 2026

Request:
POST /api/bookings
Authorization: Bearer CUSTOMER_TOKEN
{
  "courtId": "abc123",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-25T10:00:00",
  "endTime": "2026-08-25T12:00:00"
}
       │
       ▼
Security: Token validated ✅, User authenticated ✅
       │
       ▼
BookingController.createBooking(@RequestBody Booking booking)
       │
       ├─→ Spring automatically converts JSON → Booking object
       │   (Jackson library does this)
       │
       └─→ Call service:
           BookingService.createBooking(booking)
           │
           ▼
```

**Now the business logic in BookingService:**

```java
// STEP 1: Validate court exists
Court court = courtRepository.findById(booking.getCourtId())
        .orElseThrow(() -> new RuntimeException("Court not found"));

// MongoDB query: db.courts.findOne({ _id: "abc123" })
// 
// If not found → Throw exception → Controller returns 400 Bad Request
// If found → Continue
```

```java
// STEP 2: Check if court is available
if (!court.isAvailable()) {
    throw new RuntimeException("Court is not available");
}

// Court has an "isAvailable" flag
// If false (under maintenance, etc.) → Cannot book
```

```java
// STEP 3: Find potentially conflicting bookings
List<Booking> conflictingBookings = bookingRepository
        .findByCourtIdAndStartTimeBetween(
                booking.getCourtId(),
                booking.getStartTime().minusHours(2),  // Look 2h before
                booking.getEndTime().plusHours(2)      // Look 2h after
        );

// MongoDB query:
// db.bookings.find({
//   courtId: "abc123",
//   startTime: { 
//     $gte: "2026-08-25T08:00:00",  // 2h before
//     $lte: "2026-08-25T14:00:00"   // 2h after
//   }
// })
//
// Why +/- 2 hours buffer?
// To catch bookings that might overlap
//
// Example existing bookings found:
// Booking A: 09:00-10:30
// Booking B: 11:00-12:00  ← This will conflict!
// Booking C: 13:00-15:00
```

```java
// STEP 4: Check for actual time overlaps
boolean hasConflict = conflictingBookings.stream()
        .anyMatch(existingBooking ->
                !existingBooking.getStatus().equals(Booking.BookingStatus.CANCELLED) &&
                isTimeOverlapping(existingBooking, booking)
        );

if (hasConflict) {
    throw new RuntimeException("Time slot is already booked");
}

// For each existing booking:
// - Skip if cancelled (CANCELLED bookings don't block)
// - Check if times overlap

private boolean isTimeOverlapping(Booking existing, Booking newBooking) {
    return newBooking.getStartTime().isBefore(existing.getEndTime()) &&
           newBooking.getEndTime().isAfter(existing.getStartTime());
}

// Overlap logic explained:
//
// Case 1: New starts before existing ends AND new ends after existing starts
// Existing:  |----------|
// New:           |----------|
//            ↑ starts before end
//                    ↑ ends after start
// OVERLAP! ✅
//
// Case 2: No overlap
// Existing:  |-----|
// New:                  |-----|
//            ↑ starts AFTER end
// NO OVERLAP ❌
//
// Case 3: New contains existing
// Existing:    |-----|
// New:       |-----------|
// OVERLAP! ✅
//
// Case 4: Existing contains new
// Existing: |-----------|
// New:        |-----|
// OVERLAP! ✅

// Let's check our example:
// Booking B: 11:00-12:00
// New:       10:00-12:00
//
// newStart(10:00) < existingEnd(12:00) → true
// newEnd(12:00) > existingStart(11:00) → true
// Both true → OVERLAP! → Throw exception
```

```java
// STEP 5: Calculate total price
long hours = Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
booking.setTotalPrice(hours * court.getPricePerHour());

// Duration.between("2026-08-25T10:00:00", "2026-08-25T12:00:00")
// = 2 hours
//
// totalPrice = 2 * 500 = 1000

booking.setStatus(Booking.BookingStatus.CONFIRMED);
```

```java
// STEP 6: Save to database
return bookingRepository.save(booking);

// MongoDB: db.bookings.insertOne({
//   courtId: "abc123",
//   customerName: "John Doe",
//   customerEmail: "john@example.com",
//   customerPhone: "9876543210",
//   startTime: ISODate("2026-08-25T10:00:00Z"),
//   endTime: ISODate("2026-08-25T12:00:00Z"),
//   totalPrice: 1000.0,
//   status: "CONFIRMED"
// })
//
// MongoDB generates _id automatically
// Returns booking with ID
```

```
Response to client:
{
  "id": "booking123",
  "courtId": "abc123",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-25T10:00:00",
  "endTime": "2026-08-25T12:00:00",
  "totalPrice": 1000.0,
  "status": "CONFIRMED"
}
```

---

## 🗄️ Database Design & Storage

### MongoDB Collections

#### 1. users Collection

```javascript
{
  "_id": ObjectId("66c1234567890abcdef"),  // Auto-generated by MongoDB
  "name": "John Doe",
  "email": "john@example.com",              // Indexed (unique)
  "password": "$2a$10$N9qo8uLOIV6xQPQP...",  // BCrypt hash
  "phone": "9876543210",
  "roles": ["CUSTOMER"],                    // Array of roles
  "enabled": true,
  "createdAt": ISODate("2026-08-18T10:00:00.000Z"),
  "_class": "com.courtbooking.batmition.model.User"  // Spring Data adds this
}

// Index on email for fast lookups:
// db.users.createIndex({ "email": 1 }, { unique: true })
```

#### 2. courts Collection

```javascript
{
  "_id": ObjectId("66c2345678901bcdefg"),
  "name": "Court 1",
  "location": "Building A, Floor 2",
  "pricePerHour": 500.0,
  "available": true,  // Note: field name differs from Java (Lombok's isAvailable)
  "_class": "com.courtbooking.batmition.model.Court"
}
```

#### 3. bookings Collection

```javascript
{
  "_id": ObjectId("66c3456789012cdefgh"),
  "courtId": "66c2345678901bcdefg",  // Reference to court (not a MongoDB reference)
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": ISODate("2026-08-25T10:00:00.000Z"),
  "endTime": ISODate("2026-08-25T12:00:00.000Z"),
  "totalPrice": 1000.0,
  "status": "CONFIRMED",  // PENDING, CONFIRMED, CANCELLED, COMPLETED
  "_class": "com.courtbooking.batmition.model.Booking"
}

// Useful indexes:
// db.bookings.createIndex({ "courtId": 1, "startTime": 1 })
// db.bookings.createIndex({ "customerEmail": 1 })
```

### How Spring Data MongoDB Works

**Magic Method Names:**

```java
// In UserRepository interface:
Optional<User> findByEmail(String email);

// Spring Data MongoDB sees "findByEmail" and automatically generates:
// db.users.findOne({ "email": email })

// Another example:
List<Court> findByIsAvailable(boolean isAvailable);
// Generates: db.courts.find({ "available": isAvailable })

// Complex query:
List<Booking> findByCourtIdAndStartTimeBetween(
    String courtId, 
    LocalDateTime start, 
    LocalDateTime end
);
// Generates:
// db.bookings.find({
//   "courtId": courtId,
//   "startTime": { $gte: start, $lte: end }
// })
```

**The Pattern:**
- `findBy` → SELECT
- `And` → AND condition
- `Between` → BETWEEN operator
- Field names match Java class fields

---

## 🚦 Request Flow - Complete Example

### Scenario: Admin Creates a Court

```
1. CLIENT SENDS REQUEST
   
   POST http://localhost:8081/api/courts
   Headers:
     Content-Type: application/json
     Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   Body:
   {
     "name": "Court 1",
     "location": "Building A",
     "isAvailable": true,
     "pricePerHour": 500.0
   }


2. SPRING BOOT RECEIVES REQUEST
   
   Embedded Tomcat server on port 8081 receives HTTP request
   DispatcherServlet (Spring MVC) handles routing


3. SECURITY FILTER CHAIN (BEFORE CONTROLLER)
   
   Filter 1: DisableEncodeUrlFilter
   Filter 2: WebAsyncManagerIntegrationFilter
   Filter 3: SecurityContextHolderFilter
   Filter 4: HeaderWriterFilter
   Filter 5: CorsFilter
   Filter 6: LogoutFilter
   
   → Filter 7: JwtAuthenticationFilter ← OUR CUSTOM FILTER
     │
     ├─ Extract: "Authorization" header
     ├─ Parse: "Bearer eyJhbGci..."
     ├─ Validate: Token signature & expiration
     ├─ Load: User from database
     └─ Set: Authentication in SecurityContext
        
        SecurityContext now contains:
        - Principal: admin@batmition.com
        - Authorities: [ROLE_ADMIN, ROLE_CUSTOMER]
   
   Filter 8: RequestCacheAwareFilter
   Filter 9: SecurityContextHolderAwareRequestFilter
   Filter 10: AnonymousAuthenticationFilter
   Filter 11: SessionManagementFilter
   Filter 12: ExceptionTranslationFilter
   
   → Filter 13: AuthorizationFilter ← PERMISSION CHECK
     │
     ├─ Check: Endpoint = POST /api/courts
     ├─ Rule: hasRole("ADMIN")
     ├─ User has: ROLE_ADMIN ✅
     └─ Decision: ALLOW
   
   All filters passed! Continue to controller...


4. CONTROLLER LAYER
   
   @RestController
   @RequestMapping("/api/courts")
   public class CourtController {
       
       @PostMapping  ← Matches POST /api/courts
       public ResponseEntity<Court> createCourt(@RequestBody Court court) {
           
           // @RequestBody tells Spring to:
           // 1. Read request body (JSON)
           // 2. Convert to Court object (Jackson library)
           
           // Court object now contains:
           // name = "Court 1"
           // location = "Building A"
           // isAvailable = true
           // pricePerHour = 500.0
           
           // Call service layer
           Court created = courtService.createCourt(court);
           
           // Wrap in ResponseEntity with HTTP 201 status
           return ResponseEntity.status(HttpStatus.CREATED).body(created);
       }
   }


5. SERVICE LAYER
   
   @Service
   public class CourtService {
       
       private final CourtRepository courtRepository;
       
       public Court createCourt(Court court) {
           
           // Could add business logic here:
           // - Validate court name not duplicate
           // - Check if location exists
           // - Set default values
           // etc.
           
           // For now, just save to database
           return courtRepository.save(court);
       }
   }


6. REPOSITORY LAYER
   
   public interface CourtRepository extends MongoRepository<Court, String> {
       // No implementation needed!
       // Spring Data MongoDB provides implementation at runtime
   }
   
   courtRepository.save(court)
   │
   └─ Spring Data MongoDB converts Court object → MongoDB document:
      {
        "name": "Court 1",
        "location": "Building A",
        "pricePerHour": 500.0,
        "available": true,
        "_class": "com.courtbooking.batmition.model.Court"
      }


7. MONGODB
   
   Insert operation:
   db.courts.insertOne({
     "name": "Court 1",
     "location": "Building A",
     "pricePerHour": 500.0,
     "available": true,
     "_class": "com.courtbooking.batmition.model.Court"
   })
   
   MongoDB generates _id:
   {
     "_id": ObjectId("66c1234567890abcdef"),
     "name": "Court 1",
     "location": "Building A",
     "pricePerHour": 500.0,
     "available": true,
     "_class": "com.courtbooking.batmition.model.Court"
   }
   
   Returns document with _id


8. RESPONSE FLOWS BACK UP
   
   MongoDB → Repository → Service → Controller
   
   Each layer returns the Court object with generated ID


9. CONTROLLER BUILDS HTTP RESPONSE
   
   ResponseEntity<Court> with:
   - Status: 201 CREATED
   - Body: Court object
   
   Spring converts Court object → JSON (Jackson):
   {
     "id": "66c1234567890abcdef",
     "name": "Court 1",
     "location": "Building A",
     "pricePerHour": 500.0,
     "available": true
   }


10. CLIENT RECEIVES RESPONSE
    
    HTTP/1.1 201 Created
    Content-Type: application/json
    
    {
      "id": "66c1234567890abcdef",
      "name": "Court 1",
      "location": "Building A",
      "pricePerHour": 500.0,
      "available": true
    }
```

**Total time:** Usually 50-200ms depending on database speed

---

## 🔒 Security Deep Dive

### Why BCrypt for Passwords?

```
❌ Bad: Storing plain text
Database: { password: "mypassword123" }
Problem: If database leaks, all passwords exposed!

❌ Bad: Simple hash (MD5, SHA256)
Input: "password123"
MD5:   "482c811da5d5b4bc6d497ffa98491e38"
Problem: Same password = same hash
        Rainbow tables can crack it
        Too fast to compute (easy to brute force)

✅ Good: BCrypt
Input: "password123"
Round 1: Add random salt → "X7$password123"
Round 2-10: Hash 2^10 times (1024 rounds)
Output: "$2a$10$N9qo8uLOIV6xQPQP..."

Benefits:
- Random salt (same password → different hash each time)
- Slow (intentionally!) - prevents brute force
- Adaptive (can increase rounds as computers get faster)
```

### JWT Security

```
Token Structure:
header.payload.signature

Example:
eyJhbGci...  .  eyJzdWI...  .  K7xP9mL...
   ↑              ↑              ↑
 Header        Payload       Signature


Header (Base64 encoded):
{
  "alg": "HS256",  ← Algorithm
  "typ": "JWT"     ← Type
}

Payload (Base64 encoded):
{
  "sub": "user@example.com",    ← Who
  "iat": 1787030000,             ← When issued
  "exp": 1787116400              ← When expires
}

Signature:
HMAC-SHA256(
  base64(header) + "." + base64(payload),
  SECRET_KEY  ← Only server knows!
)

Why Secure?
1. Cannot be modified
   - If someone changes payload, signature won't match
   - Server detects tampering
   
2. Cannot be forged
   - Need SECRET_KEY to create valid signature
   - Only server has the key
   
3. Self-contained
   - All info in token
   - No need to query database on every request
   
4. Expiration
   - Token automatically becomes invalid after 24h
   - Must login again


Attack Scenarios:

❌ Attacker tries to change roles:
Original payload: { "sub": "user@example.com", "roles": ["CUSTOMER"] }
Modified payload: { "sub": "user@example.com", "roles": ["ADMIN"] }

Result: Signature validation fails
Server rejects token

❌ Attacker tries to extend expiration:
Original: { "exp": 1787116400 }
Modified: { "exp": 1999999999 }

Result: Signature validation fails
Server rejects token

✅ Server validates every token:
1. Check signature (tampered?)
2. Check expiration (expired?)
3. Check user exists (deleted?)
All must pass!
```

---

## 🎬 Common Scenarios Explained

### Scenario 1: Customer Tries to Create Court

```
POST /api/courts
Authorization: Bearer CUSTOMER_TOKEN

Flow:
1. JWT Filter: Validates token ✅
   User: customer@example.com
   Roles: [ROLE_CUSTOMER]

2. Authorization Filter:
   Endpoint: POST /api/courts
   Required: ROLE_ADMIN
   User has: ROLE_CUSTOMER
   
   Decision: DENY ❌

3. ExceptionTranslationFilter catches AccessDeniedException

4. Response: HTTP 403 Forbidden

Customer never reaches controller!
```

### Scenario 2: Booking Time Conflict

```
Existing booking: 10:00-12:00 on Court 1
New booking: 11:00-13:00 on Court 1

Flow:
1. Security: Pass ✅
2. Controller → Service
3. Service checks:
   - Court exists? ✅
   - Court available? ✅
   - Find existing bookings ✅
     Found: Booking{ start: 10:00, end: 12:00 }
   
   - Check overlap:
     newStart(11:00) < existingEnd(12:00)? YES
     newEnd(13:00) > existingStart(10:00)? YES
     
     Both true → OVERLAP DETECTED
   
   - Throw: "Time slot already booked" ❌

4. Controller catches exception
5. Response: HTTP 400 Bad Request

Booking NOT created!
```

### Scenario 3: Token Expired

```
User token issued: 2026-08-18 10:00:00
Token expires: 2026-08-19 10:00:00 (24h later)
Current time: 2026-08-19 15:00:00 (5h past expiration)

Request with expired token:
GET /api/bookings
Authorization: Bearer EXPIRED_TOKEN

Flow:
1. JWT Filter:
   - Extract token ✅
   - Decode payload ✅
   - Check expiration:
     exp (1787116400) < now (1787134800)
     Token expired! ❌
   
   - Validation fails
   - Authentication NOT set

2. Authorization Filter:
   No authentication in SecurityContext
   Endpoint requires authentication
   
   Decision: DENY ❌

3. Response: HTTP 401 Unauthorized

User must login again to get new token!
```

---

## 💡 Key Takeaways

1. **Every request goes through security filters FIRST**
   - Authentication (who are you?)
   - Authorization (what can you do?)
   - Only then reaches controller

2. **Layered architecture keeps code organized**
   - Controllers: HTTP handling
   - Services: Business logic
   - Repositories: Database access
   - Models: Data structure

3. **Spring does a lot automatically**
   - JSON ↔ Java object conversion
   - Database query generation
   - Security checks
   - Dependency injection

4. **Security is multilayered**
   - Passwords hashed (BCrypt)
   - Tokens signed (JWT)
   - Endpoints protected (Spring Security)
   - Roles enforced (RBAC)

5. **Database design is simple but effective**
   - No foreign keys (MongoDB is NoSQL)
   - References by ID (courtId in booking)
   - Indexes for performance (email unique)

6. **Business logic prevents conflicts**
   - Check court exists
   - Check availability
   - Check time overlaps
   - Calculate prices
   - Validate before saving

---

## 🎓 Understanding Spring Boot "Magic"

### Dependency Injection

```java
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    
    // Spring automatically provides these!
    @RequiredArgsConstructor  // Lombok creates constructor
    public BookingService(BookingRepository bookingRepository, 
                         CourtRepository courtRepository) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
    }
}

// Spring Boot startup:
// 1. Scans for @Service, @Repository, @Controller
// 2. Creates instances (beans)
// 3. Injects dependencies automatically
// 4. You never write "new BookingService()"!
```

### Auto-Configuration

```java
// You write:
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=batmition_booking

// Spring Boot automatically:
// 1. Sees MongoDB dependency in pom.xml
// 2. Reads application.properties
// 3. Creates MongoClient
// 4. Creates MongoTemplate
// 5. Configures repositories
// All ready to use!
```

### JSON Conversion

```java
@RestController
public class CourtController {
    @PostMapping("/api/courts")
    public Court createCourt(@RequestBody Court court) {
        // Spring automatically:
        // 1. Reads HTTP request body
        // 2. Parses JSON
        // 3. Creates Court object
        // 4. Validates (if @Valid annotation)
        // 5. Passes to method
        
        return court;  // Spring converts back to JSON!
    }
}
```

---

**That's the complete technical breakdown of how Batmition works!** 🎉

Every request, every security check, every database query explained from start to finish.

Questions? Check other docs or ask! 🚀
