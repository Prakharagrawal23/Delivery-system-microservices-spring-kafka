# Food Delivery Microservices

A microservice-based food delivery system built with Spring Boot, Apache Kafka (KRaft mode), and PostgreSQL. Services communicate asynchronously via Kafka topics.

## Architecture

```
Postman → Restaurant Service (8081) → PostgreSQL: restaurant_db
       → Order Service      (8082) → PostgreSQL: order_db
                                   → Kafka: order-events
                                              ↓
                              Delivery Service (8083) → PostgreSQL: delivery_db
                                                     → Kafka: delivery-status
                                                                ↓
                                         Notification Service (8084) → logs
```

## Services

| Service | Port | Description |
|---|---|---|
| restaurant-service | 8081 | Manages restaurants and menus |
| order-service | 8082 | Processes customer orders |
| delivery-service | 8083 | Assigns and tracks deliveries |
| notification-service | 8084 | Sends order tracking alerts |

## Kafka Topics

| Topic | Producer | Consumer |
|---|---|---|
| `order-events` | order-service | delivery-service |
| `delivery-status` | delivery-service | notification-service |

## Prerequisites

- Java 17+
- Gradle 8.x
- PostgreSQL 15+
- Conduktor Desktop (for Kafka KRaft broker)

## Setup

### 1. Install Conduktor Desktop

Download from https://conduktor.io/download and install. This provides the Kafka broker in KRaft mode (no Zookeeper needed).

### 2. Start Kafka

1. Open Conduktor Desktop
2. Click **Local Kafka** → **Start**
3. Broker starts on `localhost:9092`
4. Go to **Topics** → create two topics:
    - `order-events` (3 partitions, 1RF)
    - `delivery-status` (3 partitions, 1RF)

### 3. Setup PostgreSQL

Install PostgreSQL 15+ from https://postgresql.org/download

Open psql or pgAdmin as the `postgres` superuser and run:
BUT BEFORE RUNNING SETUP THE USERNAME AND PASSWORD

```sql
CREATE DATABASE restaurant_db;
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
```

### 4. Configure Each Service

Copy the example config and fill in your values:

```bash
cp restaurant-service/src/main/resources/application-example.yml \
   restaurant-service/src/main/resources/application.yml
```

Edit `application.yml` and replace:
- `YOUR_DB_USERNAME` → `postgres` (or your username)
- `YOUR_DB_PASSWORD` → your PostgreSQL password

Repeat for `order-service`, `delivery-service`, and `notification-service`.

### 5. Build the Project

```bash
./gradlew build
```

### 6. Run All Services

Open 4 terminals from the project root:

```bash
# Terminal 1
./gradlew :restaurant-service:bootRun

# Terminal 2
./gradlew :order-service:bootRun

# Terminal 3
./gradlew :delivery-service:bootRun

# Terminal 4
./gradlew :notification-service:bootRun
```

## Testing with Postman

### Create a restaurant
```
POST http://localhost:8081/api/restaurants
Content-Type: application/json

example dataset
{ "name": "Nawabi Darbar", "address": "Chowk, Lucknow", "available": true }
{ "name": "The Grill Factory", "address": "MG Road, Lucknow", "available": true }
{ "name": "Sagar Ratna", "address": "Hazratganj, Lucknow", "available": true }
{ "name": "Wok Express", "address": "Vibhuti Khand, Lucknow", "available": true }
```

### Add a menu item
```
POST http://localhost:8081/api/restaurants/1/menu
-------- change the id here before posting it.
Content-Type: application/json

example dataset --- restaurants 1
{ "name": "Galouti Kebab", "description": "Melt in mouth minced lamb kebab", "price": 320.00, "available": true }
{ "name": "Lucknowi Biryani", "description": "Dum cooked royal biryani", "price": 380.00, "available": true }
{ "name": "Sheermal", "description": "Saffron flavoured flatbread", "price": 60.00, "available": true }
{ "name": "Phirni", "description": "Chilled rice dessert", "price": 90.00, "available": true }

example dataset --- restaurants 2
{ "name": "Grilled Chicken Platter", "description": "Half chicken with sides", "price": 450.00, "available": true }
{ "name": "Seekh Kebab", "description": "Minced meat on skewer", "price": 280.00, "available": true }
{ "name": "Peri Peri Fries", "description": "Spicy crispy fries", "price": 120.00, "available": true }
{ "name": "Mango Lassi", "description": "Fresh mango yogurt drink", "price": 80.00, "available": true }

example dataset --- restaurants 3
{ "name": "Masala Dosa", "description": "Crispy dosa with filling", "price": 130.00, "available": true }
{ "name": "Rava Idli", "description": "Semolina steamed cakes", "price": 100.00, "available": true }
{ "name": "Sambar Vada", "description": "Lentil donuts in sambar", "price": 110.00, "available": true }
{ "name": "Filter Coffee", "description": "South Indian decoction", "price": 70.00, "available": true }

example dataset --- restaurants 4
{ "name": "Dimsums Platter", "description": "Steamed dimsums 6 pcs", "price": 220.00, "available": true }
{ "name": "Thai Green Curry", "description": "Coconut milk green curry", "price": 290.00, "available": true }
{ "name": "Pad Thai Noodles", "description": "Thai stir fried noodles", "price": 260.00, "available": true }
{ "name": "Bubble Tea", "description": "Taro flavoured bubble tea", "price": 150.00, "available": true }
```

### Place an order (triggers Kafka)
```
POST http://localhost:8082/api/orders
Content-Type: application/json

order 1 
{
  "restaurantId": 1, "customerId": 201,
  "deliveryAddress": "14 Hazratganj, Lucknow", "totalAmount": 850.00,
  "items": [
    { "menuItemId": 1, "itemName": "Galouti Kebab", "quantity": 2, "price": 320.00 },
    { "menuItemId": 3, "itemName": "Sheermal", "quantity": 2, "price": 60.00 },
    { "menuItemId": 4, "itemName": "Phirni", "quantity": 1, "price": 90.00 }
  ]
}

order 2
{
  "restaurantId": 2, "customerId": 202,
  "deliveryAddress": "77 Gomti Nagar, Lucknow", "totalAmount": 730.00,
  "items": [
    { "menuItemId": 5, "itemName": "Grilled Chicken Platter", "quantity": 1, "price": 450.00 },
    { "menuItemId": 7, "itemName": "Peri Peri Fries", "quantity": 1, "price": 120.00 },
    { "menuItemId": 8, "itemName": "Mango Lassi", "quantity": 2, "price": 80.00 }
  ]
}

order 3
{
  "restaurantId": 3, "customerId": 203,
  "deliveryAddress": "22 Indira Nagar, Lucknow", "totalAmount": 410.00,
  "items": [
    { "menuItemId": 9,  "itemName": "Masala Dosa", "quantity": 2, "price": 130.00 },
    { "menuItemId": 12, "itemName": "Filter Coffee", "quantity": 2, "price": 70.00 }
  ]
}

order 4 - customer 201 is placeing two orders here 
{
  "restaurantId": 4, "customerId": 201,
  "deliveryAddress": "14 Hazratganj, Lucknow", "totalAmount": 670.00,
  "items": [
    { "menuItemId": 13, "itemName": "Dimsums Platter", "quantity": 1, "price": 220.00 },
    { "menuItemId": 15, "itemName": "Pad Thai Noodles", "quantity": 1, "price": 260.00 },
    { "menuItemId": 16, "itemName": "Bubble Tea", "quantity": 1, "price": 150.00 }
  ]
}
```

After placing the order, watch the Delivery Service terminal for the auto-assignment and the Notification Service terminal for the customer alert.

### Update delivery status (triggers Kafka again)
```

---- FOR NOW IT IS UPDATING MANUALLY BUT IN FUTURE WE CAN CREATE DIFFERENT SERVICE FOR THIS 

PUT http://localhost:8083/api/deliveries/order/1/status?status=PICKED_UP&location=Restaurant
PUT http://localhost:8083/api/deliveries/order/1/status?status=IN_TRANSIT&location=City Center
PUT http://localhost:8083/api/deliveries/order/1/status?status=DELIVERED&location=Customer Address
put http://localhost:8082/api/orders/8/cancel
```

## Project Structure

```
food-delivery/
├── build.gradle
├── settings.gradle
├── restaurant-service/
├── order-service/
├── delivery-service/
└── notification-service/
```

## Tech Stack

- Spring Boot 3.2
- Spring Kafka 3.3
- Spring Data JPA
- PostgreSQL
- Apache Kafka (KRaft via Conduktor)
- Gradle 8
- Lombok
- Java 21
