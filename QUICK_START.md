# 🚀 Quick Start Guide

Get up and running with Batmition in 5 minutes!

## Prerequisites
- ✅ Java 17+ installed
- ✅ Maven installed
- ✅ MongoDB running on `localhost:27017`

## Step 1: Start the Application (30 seconds)

```bash
cd batmition
mvn spring-boot:run
```

✅ App runs on: **http://localhost:8081**

## Step 2: Register Users (1 minute)

### Create an Admin
```bash
curl -X POST http://localhost:8081/api/auth/register-admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@batmition.com",
    "password": "admin123",
    "phone": "1234567890"
  }'
```

**Save the token!** You'll get a response like:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@batmition.com",
  "name": "Admin User",
  "roles": ["ADMIN", "CUSTOMER"]
}
```

### Create a Customer
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "phone": "9876543210"
  }'
```

## Step 3: Create a Court (Admin Only) (30 seconds)

```bash
curl -X POST http://localhost:8081/api/courts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{
    "name": "Court 1",
    "location": "Building A",
    "isAvailable": true,
    "pricePerHour": 500.0
  }'
```

**Save the court ID!**

## Step 4: Create a Booking (1 minute)

```bash
curl -X POST http://localhost:8081/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_CUSTOMER_TOKEN" \
  -d '{
    "courtId": "YOUR_COURT_ID",
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "customerPhone": "9876543210",
    "startTime": "2026-08-25T10:00:00",
    "endTime": "2026-08-25T12:00:00"
  }'
```

## Step 5: View Your Booking (10 seconds)

```bash
curl -X GET "http://localhost:8081/api/bookings/my-bookings?email=john@example.com" \
  -H "Authorization: Bearer YOUR_CUSTOMER_TOKEN"
```

---

## 🎯 That's It!

You just:
- ✅ Started the app
- ✅ Registered admin and customer
- ✅ Created a court
- ✅ Made a booking
- ✅ Viewed bookings

## 📚 Next Steps

### Using Postman?
Import these collections or manually test the endpoints. See:
- **[AUTHENTICATION_GUIDE.md](AUTHENTICATION_GUIDE.md)** for auth details
- **[POSTMAN_TESTING.md](POSTMAN_TESTING.md)** for all endpoints

### Want to Learn More?
- **[README.md](README.md)** - Project overview
- **[CHANGELOG.md](CHANGELOG.md)** - What's new

---

## 🔑 Important Endpoints

| What | Endpoint | Auth Needed |
|------|----------|-------------|
| Register | `POST /api/auth/register` | No |
| Login | `POST /api/auth/login` | No |
| View courts | `GET /api/courts` | No |
| Create court | `POST /api/courts` | Yes (Admin) |
| Create booking | `POST /api/bookings` | Yes |
| My bookings | `GET /api/bookings/my-bookings?email=X` | Yes |

---

## 💡 Tips

### Postman Setup
1. Create environment variable `baseUrl` = `http://localhost:8081`
2. Save tokens in environment variables
3. Use `{{baseUrl}}/api/courts` in requests
4. Add `Authorization: Bearer {{token}}` header

### Common Issues
- **Port 8081 in use?** Kill the process or change port in `application.properties`
- **MongoDB not running?** Start MongoDB: `mongod`
- **401 Unauthorized?** Check token format: `Bearer YOUR_TOKEN`
- **403 Forbidden?** Customer trying admin action? Use admin token

---

**Ready to build? Start coding! 🚀**
