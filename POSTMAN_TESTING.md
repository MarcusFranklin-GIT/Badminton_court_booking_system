# 🏸 Batmition - Postman Testing Guide

Complete guide to test the Badminton Court Booking System API using Postman.

## 📋 Prerequisites

- Application running on `http://localhost:8081`
- MongoDB running on `localhost:27017`
- Postman installed

## 🚀 Getting Started

### Start the Application

```bash
mvn spring-boot:run
```

The server will start on **http://localhost:8081**

---

## 🎯 API Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/courts` | Create a new court |
| GET | `/api/courts` | Get all courts |
| GET | `/api/courts/available` | Get available courts only |
| GET | `/api/courts/{id}` | Get court by ID |
| PUT | `/api/courts/{id}` | Update court |
| DELETE | `/api/courts/{id}` | Delete court |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings` | Get all bookings |
| GET | `/api/bookings/{id}` | Get booking by ID |
| GET | `/api/bookings/court/{courtId}` | Get bookings for a specific court |
| GET | `/api/bookings/my-bookings?email={email}` | Get customer's bookings |
| PATCH | `/api/bookings/{id}/cancel` | Cancel a booking |

---

## 🧪 Test Sequence

Follow this order to test all features:

### 1️⃣ Create Courts

**Request:** `POST http://localhost:8081/api/courts`  
**Headers:** `Content-Type: application/json`  
**Body:**

```json
{
  "name": "Court 1",
  "location": "Building A, Floor 2",
  "isAvailable": true,
  "pricePerHour": 500.0
}
```

**Expected Response:** Status `201 Created`
```json
{
  "id": "6a83e094f1bcae5b5a004121",
  "name": "Court 1",
  "location": "Building A, Floor 2",
  "pricePerHour": 500.0,
  "available": true
}
```

**Create a second court:**
```json
{
  "name": "Court 2",
  "location": "Building B, Floor 1",
  "isAvailable": true,
  "pricePerHour": 750.0
}
```

**Create a premium court:**
```json
{
  "name": "Court 3 - Premium",
  "location": "Building A, Floor 3",
  "isAvailable": true,
  "pricePerHour": 1000.0
}
```

---

### 2️⃣ Get All Courts

**Request:** `GET http://localhost:8081/api/courts`  
**Headers:** None

**Expected Response:** Array of all courts
```json
[
  {
    "id": "6a83e094f1bcae5b5a004121",
    "name": "Court 1",
    "location": "Building A, Floor 2",
    "pricePerHour": 500.0,
    "available": true
  },
  ...
]
```

---

### 3️⃣ Get Available Courts Only

**Request:** `GET http://localhost:8081/api/courts/available`

**Expected Response:** Only courts where `isAvailable: true`

---

### 4️⃣ Get Specific Court

**Request:** `GET http://localhost:8081/api/courts/{courtId}`  
**Example:** `GET http://localhost:8081/api/courts/6a83e094f1bcae5b5a004121`

**Expected Response:** Single court object

---

### 5️⃣ Create a Booking

**Request:** `POST http://localhost:8081/api/bookings`  
**Headers:** `Content-Type: application/json`  
**Body:**

```json
{
  "courtId": "6a83e094f1bcae5b5a004121",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-20T10:00:00",
  "endTime": "2026-08-20T12:00:00"
}
```

**Expected Response:** Status `201 Created`
```json
{
  "id": "booking123",
  "courtId": "6a83e094f1bcae5b5a004121",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-20T10:00:00",
  "endTime": "2026-08-20T12:00:00",
  "totalPrice": 1000.0,
  "status": "CONFIRMED"
}
```

**Note:** `totalPrice` is calculated automatically: 2 hours × 500/hour = 1000

---

### 6️⃣ Try Creating a Conflicting Booking (Should Fail)

**Request:** `POST http://localhost:8081/api/bookings`  
**Body:**

```json
{
  "courtId": "6a83e094f1bcae5b5a004121",
  "customerName": "Jane Smith",
  "customerEmail": "jane@example.com",
  "customerPhone": "1234567890",
  "startTime": "2026-08-20T11:00:00",
  "endTime": "2026-08-20T13:00:00"
}
```

**Expected Response:** Status `400 Bad Request`  
**Reason:** Time slot overlaps with existing booking (11:00-13:00 conflicts with 10:00-12:00)

---

### 7️⃣ Create a Non-Conflicting Booking

**Request:** `POST http://localhost:8081/api/bookings`  
**Body:**

```json
{
  "courtId": "6a83e094f1bcae5b5a004121",
  "customerName": "Jane Smith",
  "customerEmail": "jane@example.com",
  "customerPhone": "1234567890",
  "startTime": "2026-08-20T14:00:00",
  "endTime": "2026-08-20T16:00:00"
}
```

**Expected Response:** Status `201 Created` (no conflict, different time slot)

---

### 8️⃣ Get All Bookings

**Request:** `GET http://localhost:8081/api/bookings`

**Expected Response:** Array of all bookings

---

### 9️⃣ Get Bookings by Court

**Request:** `GET http://localhost:8081/api/bookings/court/{courtId}`  
**Example:** `GET http://localhost:8081/api/bookings/court/6a83e094f1bcae5b5a004121`

**Expected Response:** All bookings for that specific court

---

### 🔟 Get My Bookings (Customer's Bookings)

**Request:** `GET http://localhost:8081/api/bookings/my-bookings?email=john@example.com`

**Expected Response:** All bookings for john@example.com

---

### 1️⃣1️⃣ Get Specific Booking

**Request:** `GET http://localhost:8081/api/bookings/{bookingId}`  
**Example:** `GET http://localhost:8081/api/bookings/booking123`

**Expected Response:** Single booking object

---

### 1️⃣2️⃣ Cancel a Booking

**Request:** `PATCH http://localhost:8081/api/bookings/{bookingId}/cancel`  
**Example:** `PATCH http://localhost:8081/api/bookings/booking123/cancel`

**Expected Response:**
```json
{
  "id": "booking123",
  "courtId": "6a83e094f1bcae5b5a004121",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-20T10:00:00",
  "endTime": "2026-08-20T12:00:00",
  "totalPrice": 1000.0,
  "status": "CANCELLED"
}
```

**Note:** Status changed from `CONFIRMED` to `CANCELLED`

---

### 1️⃣3️⃣ Update a Court

**Request:** `PUT http://localhost:8081/api/courts/{courtId}`  
**Headers:** `Content-Type: application/json`  
**Body:**

```json
{
  "name": "Court 1 - Premium",
  "location": "Building A, Floor 2",
  "isAvailable": true,
  "pricePerHour": 600.0
}
```

**Expected Response:** Updated court with new price

---

### 1️⃣4️⃣ Delete a Court

**Request:** `DELETE http://localhost:8081/api/courts/{courtId}`  
**Example:** `DELETE http://localhost:8081/api/courts/6a83e094f1bcae5b5a004121`

**Expected Response:** Status `204 No Content`

---

## 🎮 Complete Test Workflow

Follow this workflow to test the entire system:

1. ✅ **Create 3 courts** (save their IDs)
2. ✅ **Get all courts** (verify they exist)
3. ✅ **Get available courts** (check filtering works)
4. ✅ **Create a booking** using first court's ID
5. ✅ **Try creating a conflicting booking** (should fail with 400)
6. ✅ **Create a non-conflicting booking** (different time slot)
7. ✅ **Get all bookings** (see all bookings)
8. ✅ **Get bookings by court ID** (filter by court)
9. ✅ **Get bookings by email** (filter by customer)
10. ✅ **Get specific booking** (by booking ID)
11. ✅ **Cancel a booking** (status changes to CANCELLED)
12. ✅ **Update a court** (change price or availability)
13. ✅ **Delete a court** (remove it)

---

## 📝 Sample Test Data

### Sample Courts

```json
// Court 1 - Budget
{
  "name": "Court 1 - Budget",
  "location": "Building A, Floor 1",
  "isAvailable": true,
  "pricePerHour": 500.0
}

// Court 2 - Standard
{
  "name": "Court 2 - Standard",
  "location": "Building B, Floor 1",
  "isAvailable": true,
  "pricePerHour": 750.0
}

// Court 3 - Premium
{
  "name": "Court 3 - Premium",
  "location": "Building A, Floor 3",
  "isAvailable": true,
  "pricePerHour": 1000.0
}

// Court 4 - Under Maintenance
{
  "name": "Court 4 - Maintenance",
  "location": "Building C",
  "isAvailable": false,
  "pricePerHour": 500.0
}
```

### Sample Bookings

```json
// Morning Slot
{
  "courtId": "COURT_ID_HERE",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "startTime": "2026-08-20T08:00:00",
  "endTime": "2026-08-20T10:00:00"
}

// Afternoon Slot
{
  "courtId": "COURT_ID_HERE",
  "customerName": "Jane Smith",
  "customerEmail": "jane@example.com",
  "customerPhone": "1234567890",
  "startTime": "2026-08-20T14:00:00",
  "endTime": "2026-08-20T16:00:00"
}

// Evening Slot
{
  "courtId": "COURT_ID_HERE",
  "customerName": "Bob Wilson",
  "customerEmail": "bob@example.com",
  "customerPhone": "5555555555",
  "startTime": "2026-08-20T18:00:00",
  "endTime": "2026-08-20T20:00:00"
}
```

---

## 🐛 Common Issues & Solutions

### Issue: Port 8080 already in use

**Solution:** The app now runs on port **8081**. Use:
```
http://localhost:8081
```

### Issue: MongoDB connection refused

**Solution:** Start MongoDB:
```bash
# Make sure MongoDB is running
mongosh  # Test connection
```

### Issue: 400 Bad Request on booking

**Possible Reasons:**
1. Time slot conflict with existing booking
2. Court doesn't exist (invalid `courtId`)
3. Court is not available (`isAvailable: false`)

### Issue: Empty array response

**Solution:** This is normal if no data exists yet. Create some courts first!

---

## 💡 Postman Tips

### Save as Collection
1. Create a new collection called "Batmition API"
2. Save all requests in the collection
3. Use variables like `{{baseUrl}}` for easy switching

### Environment Variables
Create a Postman environment with:
- `baseUrl`: `http://localhost:8081`
- `courtId`: Save court ID from response
- `bookingId`: Save booking ID from response

### Test Scripts
Add this to your POST requests to auto-save IDs:
```javascript
// For court creation
pm.environment.set("courtId", pm.response.json().id);

// For booking creation
pm.environment.set("bookingId", pm.response.json().id);
```

---

## 🎯 Quick cURL Examples

### Create Court
```bash
curl -X POST http://localhost:8081/api/courts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Court 1",
    "location": "Building A",
    "isAvailable": true,
    "pricePerHour": 500.0
  }'
```

### Get All Courts
```bash
curl http://localhost:8081/api/courts
```

### Create Booking
```bash
curl -X POST http://localhost:8081/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "courtId": "COURT_ID_HERE",
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "customerPhone": "9876543210",
    "startTime": "2026-08-20T10:00:00",
    "endTime": "2026-08-20T12:00:00"
  }'
```

---

## 📚 Additional Resources

- [README.md](README.md) - Project overview
- MongoDB Database: `batmition_booking`
- Collections: `courts`, `bookings`

---

## ✅ Validation Rules

### Court Validation
- `name`: Required, not empty
- `location`: Required
- `isAvailable`: Boolean
- `pricePerHour`: Must be positive number

### Booking Validation
- `courtId`: Must exist in database
- `startTime`: Must be in the future
- `endTime`: Must be after startTime
- No time slot conflicts for same court
- Court must be available

### Booking Status Flow
```
PENDING → CONFIRMED → COMPLETED
            ↓
        CANCELLED
```

---

**Happy Testing! 🎉**

For questions or issues, check the main [README.md](README.md)
