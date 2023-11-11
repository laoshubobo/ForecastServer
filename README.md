# ForecastServer
The http endpoint is for retrieve weather short forecast from National Weather Service API Web Service 
To use it:
Clone the project, compile it, and open ForecastHttpServer object to run
Open terminal to curl or input the api url in browser to enter to get the result

Example: 
curl http://localhost:8080/api/forecast?latitude=38.6484&longitude=-121.7339
Response:
{"shortForecast":"Sunny","CharacterizedTemperature":"Moderate"}
