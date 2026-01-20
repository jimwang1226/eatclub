# EatClub API

A Spring Boot REST API for querying restaurant deals and peak times.

## Tech Stack

- Java 17
- Spring Boot 3.5
- WebClient (for external data fetching)

## Quick Start

```bash
mvn spring-boot:run
```

Server runs at `http://localhost:8080`

## API Endpoints

### 1. Get Active Deals

Returns all active restaurant deals available at a specific time.

```
GET /deals?timeOfDay={time}
```

**Parameters:**

| Name | Type | Required | Description                                        |
|------|------|----------|----------------------------------------------------|
| timeOfDay | string | Yes | Time in HH:mm format (e.g., "11:00", or "11:00am") |

**Example:**

```bash
curl "http://localhost:8080/deals?timeOfDay=11:00"
```

**Response:**

```json
{
  "deals": [
    {
      "restaurantObjectId": "abc123",
      "restaurantName": "Pizza Palace",
      "restaurantAddress1": "123 Main St",
      "restaurantSuburb": "Melbourne",
      "restaurantOpen": "10:00",
      "restaurantClose": "22:00",
      "dealObjectId": "deal001",
      "discount": "20",
      "dineIn": "true",
      "lightning": "false",
      "qtyLeft": "5"
    }
  ]
}
```

### 2. Get Peak Time

Returns the calculated peak time period with the most active deals.

```
GET /peaktime
```

**Example:**

```bash
curl "http://localhost:8080/peaktime"
```

**Response:**

```json
{
  "peakTimeStart": "12:00",
  "peakTimeEnd": "13:00"
}
```

