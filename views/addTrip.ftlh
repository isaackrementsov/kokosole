<#include "partials/header.ftlh">
    <form class="profile" action="/trip/add" method="post">
        <input type="hidden" name="locations">
        <div class="profile-left">
            <div class="profile-top">
                <i class="material-icons" style="font-size: 14vw; color: #F76D6E">terrain</i>
                <h1>Add Trip</h1>
            </div>
            <div class="profile-bottom">
                <label for="name" class="label">Name</label>
                <input type="text" name="name">
                <p>
                    Trips are used to signify that you are travelling. 
                    Add locations and activities to join other users on your trip.
                </p>
                    <input type="submit" class="smaller navlink cta" style="background-color: #F76D6E" value="Save trip">
            </div>
        </div>
        <div class="profile-right">
            <div class="profile-top">
                <h1>Locations</h1>
                <div id="locations"></div>
            </div>
            <div class="profile-bottom">
                <div class="inline form-side" style="margin-right: 12%">
                    <h2 style="color:grey; font-size: 1.7vw; font-weight: 300">Activities</h2>
                    <div id="activities"></div>
                    <div class="activity-form">
                        <label for="location-name" class="label">Activity name</label>
                        <input type="text" name="activity-name"> 
                        <div class="dates">
                            <label for="location-name" class="label">Dates (mm/dd/yyyy)</label>
                            <input type="text" class="inline" name="start" placeholder="Start">  
                            <input type="text" class="inline" name="end" placeholder="End">
                        </div>    
                        <label for="location-name" class="label">Users</label>
                        <input list="users" type="text" name="participants">  
                        <datalist id="users">
                                <option value="Maria Ramirez">
                        </datalist>
                        <div class="user-ids">
                            <div id="Maria Ramirez" style="display:none">
                                    some-id
                            </div>
                        </div>
                        <button type="button" class="smaller navlink" style="font-weight: 400; background-color: #FFCCCD; font-size: 1.05vw; margin-top: 3vh" onclick="addActivity()">
                            Save activity
                        </button>                
                    </div>
                </div>
                <div class="inline form-side">
                    <div style="margin-bottom: 5vh">
                        <label for="location-name" class="label">Location name</label>
                        <input type="text" name="location-name">
                    </div>
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="location-name" class="label">Town</label>
                        <input type="text" name="town">
                    </div>
                    <div class="inline">
                        <label for="location-name" class="label">ZIP</label>
                        <input type="number" name="zip">
                    </div>
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="location-name" class="label">Country</label>
                        <input type="text" name="country">
                    </div>
                    <div class="inline">
                        <label for="location-name" class="label">Subdivision</label>
                        <input type="text" name="subdivision">
                    </div>
                </div>
                <div class="profile-bottom">
                    <button type="button" class="smaller navlink cta" onclick="addLocation()" style="background-color: #F76D6E">Save location</button>
                </div>
            </div>
        </div>
    </form>
    <script>
        var locations = [];
        function addLocation(){
            var locations = document.getElementById("location");
            var name = document.getElementsByName("location-name")[0].value;
            var town = document.getElementsByName("town")[0].value;
            var subdivision = document.getElementsByName("subdivision")[0].value;
            var country = document.getElementsByName("country")[0].value;
            var zip = document.getElementsByName("zip")[0].value;
            var activities = JSON.parse(document.getElementById("activities").value);
            locations.push(
                {name: name, town: town, subdivision: subdivision, country: country, zip: zip, activities: activities}
            );
            locations.innerHTML += `<div class="box">${name}</div>`;
        }
        function addActivity(){
            var activities = document.getElementById("activities");
            var name = document.getElementsByName("name")[0].value;
            var start = document.getElementsByName("start")[0].value;
            var end = document.getElementsByName("end")[0].value;
            var participants = JSON.parse(document.getElementById("participants"));
            activities.value += JSON.stringify(
                {name: name, start: new Date(start), end: new Date(end), participants: participants}
            );
            activites.innerHTML += `<div class="box">${name}</div>`;
        }
        function addLocations(){
            var locations = document.getElementsByName("locations");
            locations.value = JSON.stringify(locations);
        }
    </script>
<#include "partials/footer.ftlh">