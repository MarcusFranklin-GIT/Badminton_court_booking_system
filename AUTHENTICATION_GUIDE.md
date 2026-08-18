# 🔐 Authentication & Authorization Guide

Complete guide for using JWT authentication and role-based access control in the Batmition API.

## 📋 Overview

The application now includes:
- ✅ **JWT Token-based Authentication**
- ✅ **User Roles:** `CUSTOMER` and `ADMIN`
- ✅ **Secure Password Storage** (BCrypt hashing)
- ✅ **Role-based Access Control**

---

## 🚀 Quick Start

### Step 1: Register a Customer

**Endpoint:** `POST /api/auth/register`  
**Access:** Public (no authentication required)

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "9876543210"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...",
  "email": "john@example.com",
  "name": "John Doe",
  "roles": ["CUSTOMER"]
}
```

### Step 2: Register an Admin

**Endpoint:** `POST /api/auth/register-admin`  
**Access:** Public (use cautiously - consider protecting this in production)

**Request Body:**
```json
{
  "name": "Admin User",
  "email": "admin@batmition.com",
  "password": "admin123",
  "phone": "1234567890"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...",
  "email": "admin@batmition.com",
  "name": "Admin User",
  "roles": ["ADMIN", "CUSTOMER"]
}
```

**Note:** Admins automatically get both `ADMIN` and `CUSTOMER` roles.

### Step 3: Login

**Endpoint:** `POST /api/auth/login`  
**Access:** Public

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...",
  "email": "john@example.com",
  "name": "John Doe",
  "roles": ["CUSTOMER"]
}
```

### Step 4: Use the Token

For all authenticated requests, add the JWT token to the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWI...
```

---

## 🔒 Access Control Rules

| Endpoint | Method | Access Level | Description |
|----------|--------|-------------|-------------|
| `/api/auth/register` | POST | Public | Register as customer |
| `/api/auth/register-admin` | POST | Public | Register as admin |
| `/api/auth/login` | POST | Public | Login |
| `/api/courts` | GET | Public | View courts |
| `/api/courts/{id}` | GET | Public | View court details |
| `/api/courts/available` | GET | Public | View available courts |
| `/api/courts` | POST | **ADMIN only** | Create court |
| `/api/courts/{id}` | PUT | **ADMIN only** | Update court |
| `/api/courts/{id}` | DELETE | **ADMIN only** | Delete court |
| `/api/bookings/**` | ALL | **Authenticated** | All booking operations |

---

## 🧪 Testing with Postman

### Test Flow 1: Customer Journey

#### 1. Register as Customer
```
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "password123",
  "phone": "1234567890"
}
```

**Save the token from the response!**

#### 2. View Available Courts (No Auth Needed)
```
GET http://localhost:8081/api/courts/available
```

#### 3. Create a Booking (Auth Required)
```
POST http://localhost:8081/api/bookings
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "courtId": "COURT_ID",
  "customerName": "Jane Doe",
  "customerEmail": "jane@example.com",
  "customerPhone": "1234567890",
  "startTime": "2026-08-20T10:00:00",
  "endTime": "2026-08-20T12:00:00"
}
```

#### 4. View My Bookings
```
GET http://localhost:8081/api/bookings/my-bookings?email=jane@example.com
Authorization: Bearer YOUR_TOKEN_HERE
```

#### 5. Try to Create a Court (Should Fail - Not Admin)
```
POST http://localhost:8081/api/courts
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "name": "Court 1",
  "location": "Building A",
  "isAvailable": true,
  "pricePerHour": 500.0
}
```

**Expected:** `403 Forbidden` (Customer doesn't have ADMIN role)

---

### Test Flow 2: Admin Journey

#### 1. Register as Admin
```
POST http://localhost:8081/api/auth/register-admin
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@batmition.com",
  "password": "admin123",
  "phone": "1234567890"
}
```

**Save the token!**

#### 2. Create a Court (Admin Only)
```
POST http://localhost:8081/api/courts
Authorization: Bearer ADMIN_TOKEN_HERE
Content-Type: application/json

{
  "name": "Court 1",
  "location": "Building A",
  "isAvailable": true,
  "pricePerHour": 500.0
}
```

**Expected:** `201 Created` ✅

#### 3. Update a Court
```
PUT http://localhost:8081/api/courts/{courtId}
Authorization: Bearer ADMIN_TOKEN_HERE
Content-Type: application/json

{
  "name": "Court 1 - Premium",
  "location": "Building A",
  "isAvailable": true,
  "pricePerHour": 600.0
}
```

#### 4. Create a Booking (Admin Can Also Book)
```
POST http://localhost:8081/api/bookings
Authorization: Bearer ADMIN_TOKEN_HERE
Content-Type: application/json

{
  "courtId": "COURT_ID",
  "customerName": "Admin User",
  "customerEmail": "admin@batmition.com",
  "customerPhone": "1234567890",
  "startTime": "2026-08-20T14:00:00",
  "endTime": "2026-08-20T16:00:00"
}
```

#### 5. Delete a Court
```
DELETE http://localhost:8081/api/courts/{courtId}
Authorization: Bearer ADMIN_TOKEN_HERE
```

---

## 📝 Postman Setup

### Using Environment Variables

Create a Postman environment with these variables:

**Variable** | **Initial Value** | **Description**
---|---|---
`baseUrl` | `http://localhost:8081` | API base URL
`customerToken` | (empty) | Customer JWT token
`adminToken` | (empty) | Admin JWT token
`courtId` | (empty) | Court ID for testing
`bookingId` | (empty) | Booking ID for testing

### Auto-Save Tokens

Add this **Test Script** to your registration/login requests:

```javascript
// For customer registration/login
if (pm.response.code === 200 || pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("customerToken", response.token);
    console.log("Customer token saved:", response.token);
}

// For admin registration/login
if (pm.response.code === 200 || pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("adminToken", response.token);
    console.log("Admin token saved:", response.token);
}
```

### Using Tokens

In your requests, use:
```
Authorization: Bearer {{customerToken}}
```
or
```
Authorization: Bearer {{adminToken}}
```

---

## 🔑 User Roles Explained

### CUSTOMER Role
- ✅ Can register and login
- ✅ Can view all courts (GET)
- ✅ Can create bookings
- ✅ Can view their own bookings
- ✅ Can cancel their own bookings
- ❌ Cannot create/update/delete courts

### ADMIN Role
- ✅ All CUSTOMER permissions
- ✅ Can create courts
- ✅ Can update courts
- ✅ Can delete courts
- ✅ Can manage all bookings

---

## 🔐 Security Features

### Password Security
- Passwords are hashed using **BCrypt** with salt
- Passwords are never stored in plain text
- Strong one-way encryption

### JWT Token Security
- Token expires after **24 hours**
- Token includes user email in payload
- Signed with HMAC SHA-256 algorithm
- Validated on every protected request

### Endpoint Protection
- Public endpoints: Auth and GET courts
- Protected endpoints: All bookings
- Admin-only endpoints: Court management (POST/PUT/DELETE)

---

## 🚨 Common Issues & Solutions

### Issue: 401 Unauthorized

**Reasons:**
1. No token provided
2. Invalid token
3. Token expired (24 hours)
4. Wrong Authorization header format

**Solution:**
```
// Correct format
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

// Wrong formats ❌
Authorization: eyJhbGciOiJIUzI1NiJ9...
Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Issue: 403 Forbidden

**Reason:** User doesn't have required role

**Solution:**
- Use admin account for court management
- Customer accounts cannot create/update/delete courts

### Issue: 400 Bad Request on Registration

**Reasons:**
1. Email already exists
2. Missing required fields
3. Invalid email format

**Solution:**
- Use unique email addresses
- Ensure all fields are provided

### Issue: Token Not Working

**Check:**
1. Copy the full token (no truncation)
2. Token hasn't expired (24h limit)
3. Using correct header format
4. User account is enabled

---

## 💡 Best Practices

### Development
1. **Use different accounts** for testing customer and admin flows
2. **Save tokens** in Postman environment variables
3. **Test unauthorized access** to ensure security works
4. **Check token expiration** - refresh after 24 hours

### Production Recommendations
1. **Protect the register-admin endpoint** - add secret key or remove it
2. **Use environment variables** for JWT secret key
3. **Implement token refresh** mechanism
4. **Add rate limiting** to prevent brute force
5. **Enable HTTPS** in production
6. **Add email verification** for new accounts
7. **Implement password reset** functionality
8. **Add audit logging** for admin actions

---

## 📊 Token Structure

JWT tokens have three parts separated by dots:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzg3MDMwMDAwLCJleHAiOjE3ODcxMTY0MDB9.signature
```

**Header** | **Payload** | **Signature**
---|---|---
Algorithm: HS256 | Email (sub) | HMAC SHA-256
 | Issued At (iat) | 
 | Expiration (exp) | 

You can decode tokens at [jwt.io](https://jwt.io) (payload only, signature requires secret)

---

## 🧪 Complete Test Scenarios

### Scenario 1: Customer Cannot Manage Courts

1. Register as customer
2. Try to create a court → **403 Forbidden** ✅
3. Try to update a court → **403 Forbidden** ✅
4. Try to delete a court → **403 Forbidden** ✅

### Scenario 2: Admin Can Do Everything

1. Register as admin
2. Create a court → **201 Created** ✅
3. Create a booking → **201 Created** ✅
4. Update the court → **200 OK** ✅
5. Delete the court → **204 No Content** ✅

### Scenario 3: Unauthenticated Access

1. Try to create booking without token → **401 Unauthorized** ✅
2. Try to view my bookings without token → **401 Unauthorized** ✅
3. View available courts without token → **200 OK** ✅

### Scenario 4: Expired Token

1. Wait 24 hours (or manually modify token)
2. Try to access protected endpoint → **401 Unauthorized** ✅
3. Login again to get new token

---

## 🔄 API Response Codes

| Code | Meaning | When It Happens |
|------|---------|-----------------|
| 200 | OK | Successful GET/PUT/PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid data or duplicate email |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |

---

## 📚 Additional Resources

- [JWT Introduction](https://jwt.io/introduction)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [BCrypt Password Hashing](https://en.wikipedia.org/wiki/Bcrypt)

---

## 🎯 Next Steps

Now that authentication is working, consider adding:

- [ ] Password reset via email
- [ ] Email verification on registration
- [ ] Refresh token mechanism
- [ ] User profile management
- [ ] Change password endpoint
- [ ] Account deactivation
- [ ] Admin dashboard to manage users
- [ ] Booking history for customers
- [ ] OAuth2 integration (Google, Facebook)
- [ ] Two-factor authentication (2FA)

---

**🎉 Your API is now secure with JWT authentication and role-based access control!**

For general API testing, see [POSTMAN_TESTING.md](POSTMAN_TESTING.md)
