# Emlakburada Package Service

## Running the Service

The Gateway Service runs on `http://localhost:8080`.

## API Endpoints

### Packages

#### Purchase a Packet
**POST** `/api/v1/packages`

**Request:**
```json
{
    "userId": 2,
    "packageId": 3,
    "paymentDetails": {
        "cardNumber": "124534234534",
        "expiryDate": "11/28",
        "cvv": 560
    }
}
```

#### Get All Ad Packages
**GET** `/api/v1/packages`


#### Get Ad Packages By Id
**GET** `/api/v1/packages/{id}`


#### Get User Packages By User Id
**GET** `/api/v1/packages/user/{id}`
