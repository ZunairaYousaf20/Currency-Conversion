# **Currency Conversion and Bill Calculation Service**
## **Overview**
This project provides an API for calculating the total payable amount for a bill after applying 
applicable discounts based on the user type (employee, affiliate, or regular customer) and customer 
tenure. The service also integrates with an external currency conversion API to calculate amounts 
across different currencies.

## **Technologies Used**
* Java 17
* Spring Boot 3.x
* JUnit 5 (for testing)
* Mockito (for mocking)
* RestTemplate (for HTTP requests)
* H2 Database (for testing)
* Maven (for dependency management)

## **Features**
* User type-based discounts (Employee, Affiliate, Registered Customer).
* Special discount if customer has been registered for more than 2 years.
* Calculation of discounts only on non-grocery items.
* Conversion of bill amount from one currency to another using an external API.

## **How to Run the Application**
1. Clone the repository:
`git clone https://github.com/your-repo/currency-conversion-service.git cd currency-conversion-service`
2. Set up your API keys in the `application.properties` file:
`api.base.url=http://api.exchangerate-api.com/v4/latest
api.key=your-api-key-here`
3. Run the application using Maven:
`mvn spring-boot:run`

## **How to Run Tests**
1. Run the tests using Maven:
`mvn clean install`

## **External Integration**
This service integrates with the ExchangeRate API for converting the bill amount between different 
currencies.<br>

Make sure you have a valid API key and update the following properties in `application.properties`:<br> 

`api.base.url=http://api.exchangerate-api.com/v4/latest` <br>
`api.key=your-api-key-here`

## **Endpoints**
###### **POST** `/api/create-user`

#### **Request Body**

`{
"name": "XYZ",
"email": "xyz@example.com",
"phoneNo": "1234567890",
"userType": "EMPLOYEE",
"password": "Pass@234",
"role": "ADMIN",
"joiningDate": "2020-01-15"
}`

#### **Response**
`{
"name": "XYZ",
"email": "xyz@example.com",
"phoneNo": "1234567890",
"userType": "EMPLOYEE",
"joiningDate": "2020-01-15"
}`

###### **POST** `/api/authenticate`: 
In order to Authenticate, You need to generate JWT token, this will be used to access `/api/calculate` url for bill calculation in targeted currency

#### **Request Body**

`{
"userName": "XYZ",
"password": "Pass@234"
}
`

###### **POST** `/api/calculate`

#### **Request Body**
`{
"user": {
"userId": "1",
"userName": "XYZ",
"userType": "EMPLOYEE",
"joiningDate": "2022-01-15"
},
"items": [
{
"itemId": 101,
"category": "Electronics",
"price": 100.00,
"quantity": 2
},
{
"itemId": 102,
"category": "Grocery",
"price": 50.00,
"quantity": 1
}
],
"sourceCurrency": "USD",
"targetCurrency": "PKR",
"totalAmount": 150.00
}`

#### **Response**
`{
"totalPayableAmount": 31935.50000
}`