## Mapsense

It is a weather app where you can find the climate details (like temperature, weather, etc.) of any city on this planet. Just search your city with the city name and that's it.

### Libraries used:
* Mapbox - To render map
* Retrofit and Gson - To access REST APIs
* Hilt - For Dependency Injection
* Glide - To load images

### Design explanation:
There is only a single screen for this app which is used to show the map. The city-specific weather information is not big enough to show on a separate screen that's why used a dialog to show that information.
There is a search box where you can enter the city name and by pressing the **Search city** button (disabled if nothing is entered in the search box) whether you will get weather information or multiple cities 
with the same name to choose which you want.

There is also a round button on the screen which accesses your current location and gathers climate details of your location after asking for location permission if not already given.

### Some application screenshots

|My Location | Find any city | 
|:----------------:|:----------------:|
| <img src="https://github.com/Coder481/MapsenseAssignment/assets/68111551/cd700813-db75-45a5-98d6-07e0c9ee12bd" width="250" height="500"/>| <img src="https://github.com/Coder481/MapsenseAssignment/assets/68111551/1400d9aa-4bfc-4a72-ae37-3d9b55dddf44" width="250" height="500"/>


### Application Demo

https://github.com/Coder481/MapsenseAssignment/assets/68111551/a5fb0e0a-f846-4e68-a88d-ef4bf8d8cd95

