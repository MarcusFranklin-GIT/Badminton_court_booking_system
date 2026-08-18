# 🏸 Batmition - Badminton Court Booking System

A simple badminton court booking system built with Spring Boot and MongoDB. Perfect for learning vibe coding and building practical applications!

## 🚀 Tech Stack
- **Backend**: Java 17/21, Spring Boot 3.2.0
- **Database**: MongoDB
- **Build Tool**: Maven
- **Libraries**: Lombok, Spring Data MongoDB, Validation

## ✨ Features
- ✅ Manage courts (CRUD operations)
- ✅ Book time slots for courts with conflict detection
- ✅ Check court availability
- ✅ View bookings by court or customer
- ✅ Cancel bookings
- ✅ Automatic price calculation based on duration
- ✅ Booking status tracking (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- ✅ REST API with proper HTTP status codes

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

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/courts` | Create a new court |
| GET | `/api/courts` | Get all courts |
| GET | `/api/courts/available` | Get available courts |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings` | Get all bookings |
| GET | `/api/bookings/my-bookings?email={email}` | Get customer's bookings |
| PATCH | `/api/bookings/{id}/cancel` | Cancel a booking |

**📖 Full API Documentation:** See [POSTMAN_TESTING.md](POSTMAN_TESTING.md) for complete API reference with examples.

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
│   ├── model/           # Domain models (Court, Booking)
│   ├── repository/      # MongoDB repositories
│   ├── service/         # Business logic
│   ├── controller/      # REST API endpoints
│   └── BatmitionApplication.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
├── README.md
└── POSTMAN_TESTING.md   # Complete API testing guide
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
- [ ] Add authentication (Spring Security + JWT)
- [ ] Add user roles (Admin, Customer)
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
