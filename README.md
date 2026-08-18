# 🏸 Batmition - Badminton Court Booking System

A simple badminton court booking system built with Spring Boot and MongoDB. Perfect for learning vibe coding and building practical applications!

## 🚀 Tech Stack
- **Backend**: Java 17/21, Spring Boot 3.2.0
- **Database**: MongoDB
- **Build Tool**: Maven
- **Libraries**: Lombok, Spring Data MongoDB, Validation

## ✨ Features
- ✅ **JWT Authentication** - Secure token-based authentication
- ✅ **Role-Based Access Control** - Admin and Customer roles
- ✅ **User Management** - Registration and login
- ✅ **Manage Courts** - CRUD operations (Admin only)
- ✅ **Smart Booking System** - Time slot booking with conflict detection
- ✅ **Court Availability** - Real-time availability check
- ✅ **Booking Management** - View, create, and cancel bookings
- ✅ **Automatic Pricing** - Price calculation based on duration
- ✅ **Booking Status Tracking** - PENDING, CONFIRMED, CANCELLED, COMPLETED
- ✅ **Secure Passwords** - BCrypt hashing
- ✅ **REST API** - Clean API with proper HTTP status codes

## 📋 Prerequisites
- Java 17 or higher (tested with Java 21)
- Maven 3.6+
- MongoDB running locally on port 27017

## 🎯 Quick Start

### 1. Start MongoDB
```bash
# Make sure MongoDB is running on localhost:27017
mongosh  # Test your connection
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8081`

## 📚 API Documentation

### Quick API Overview

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register as customer |
| POST | `/api/auth/register-admin` | Public | Register as admin |
| POST | `/api/auth/login` | Public | Login |
| POST | `/api/courts` | 🔒 Admin | Create a new court |
| GET | `/api/courts` | Public | Get all courts |
| GET | `/api/courts/available` | Public | Get available courts |
| POST | `/api/bookings` | 🔒 Auth | Create a booking |
| GET | `/api/bookings` | 🔒 Auth | Get all bookings |
| GET | `/api/bookings/my-bookings?email={email}` | 🔒 Auth | Get customer's bookings |
| PATCH | `/api/bookings/{id}/cancel` | 🔒 Auth | Cancel a booking |

**📖 Complete Documentation:**
- **[HOW_IT_WORKS.md](HOW_IT_WORKS.md)** - 🧠 Complete technical explanation (START HERE!)
- **[QUICK_START.md](QUICK_START.md)** - Get running in 5 minutes
- **[AUTHENTICATION_GUIDE.md](AUTHENTICATION_GUIDE.md)** - JWT auth, user roles, security
- **[POSTMAN_TESTING.md](POSTMAN_TESTING.md)** - Complete API testing guide

## 🧪 Testing with Postman

For complete step-by-step testing guide with all endpoints and sample data, check out:

**👉 [POSTMAN_TESTING.md](POSTMAN_TESTING.md)**

### Quick Test
```bash
# Create a court
curl -X POST http://localhost:8081/api/courts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Court 1",
    "location": "Building A",
    "isAvailable": true,
    "pricePerHour": 500.0
  }'

# Get all courts
curl http://localhost:8081/api/courts
```

## 🏗️ Project Structure
```
batmition/
├── src/main/java/com/courtbooking/batmition/
│   ├── model/           # Domain models (User, Court, Booking)
│   ├── repository/      # MongoDB repositories
│   ├── service/         # Business logic
│   ├── controller/      # REST API endpoints
│   ├── security/        # JWT & Spring Security config
│   ├── dto/             # Data Transfer Objects
│   └── BatmitionApplication.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
├── README.md
├── AUTHENTICATION_GUIDE.md    # JWT auth & roles guide
└── POSTMAN_TESTING.md         # API testing guide
```

## 💡 What is Vibe Coding?

This project demonstrates vibe coding principles:
- ✅ **Start simple** - Basic CRUD operations first
- ✅ **Build incrementally** - Add features as needed
- ✅ **Make it work first** - Optimization comes later
- ✅ **Clean architecture** - But not over-engineered
- ✅ **Practical learning** - Real-world use case

## 🎯 What's Next?

This is the MVP! Here are ideas to extend it:

### Backend Enhancements
- [x] ✅ Add authentication (Spring Security + JWT)
- [x] ✅ Add user roles (Admin, Customer)
- [ ] Payment integration (Stripe/Razorpay)
- [ ] Email notifications
- [ ] SMS notifications
- [ ] Peak/off-peak pricing
- [ ] Recurring bookings
- [ ] Waiting list feature
- [ ] Court maintenance scheduling

### Frontend
- [ ] React/Vue/Angular web app
- [ ] Mobile app (React Native/Flutter)
- [ ] Admin dashboard
- [ ] Customer portal
- [ ] Real-time availability calendar

### DevOps
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Cloud deployment (AWS/Azure/GCP)
- [ ] Monitoring and logging
- [ ] API rate limiting

### Data & Analytics
- [ ] Booking analytics
- [ ] Revenue reports
- [ ] Popular time slots
- [ ] Customer behavior insights

**Pick what excites you and keep vibe coding! 🚀**
