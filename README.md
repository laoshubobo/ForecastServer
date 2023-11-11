# ForecastServer
The HTTP endpoint is for retrieving weather short forecasts from the National Weather Service API Web Service 
To use it:
Clone the project, compile it, and open the ForecastHttpServer object to run
Open the terminal to curl or input the API URL in the browser

Example: 
curl http://localhost:8080/api/forecast?latitude=38.6484&longitude=-121.7339 

 Response:
{"shortForecast":"Sunny","CharacterizedTemperature":"Moderate"}
