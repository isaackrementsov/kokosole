<#include "partials/header.ftlh">
    <form class="profile" action="/trip/add" method="post" onsubmit="return addLocations()">
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
                        <div id="participants"></div>
                        <input type="text" name="participants" placeholder="Separate emails with ','">
                        <div id="user-ids"></div>
                        <button type="button" class="smaller navlink" style="font-weight: 400; background-color: #FFCCCD; font-size: 1.05vw; margin-top: 3vh" onclick="addActivity()">
                            Save activity
                        </button>                
                    </div>
                </div>
                <div class="inline form-side aligntop">
                    <div style="margin-bottom: 5vh">
                        <label for="location-name" class="label">Location name</label>
                        <input type="text" name="location-name">
                    </div>
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="location-name" class="label">City</label>
                        <input type="text" name="town">
                    </div>
                    <div class="inline">
                        <label for="location-name" class="label">Postal Code</label>
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
        var activities = document.getElementById("activities");
        var participantsForm = document.getElementsByName("participants")[0];
        var participants = document.getElementById("participants");
        participants.value = [];
        activities.value = [];
        function addLocation(){
            var locationsDiv = document.getElementById("locations");
            var name = document.getElementsByName("location-name")[0];
            var town = document.getElementsByName("town")[0];
            var subdivision = document.getElementsByName("subdivision")[0];
            var country = document.getElementsByName("country")[0];
            var zip = document.getElementsByName("zip")[0];
            locations.push(
                {
                    name: name.value, 
                    town: town.value, 
                    subdivision: subdivision.value, 
                    country: country.value, 
                    zip: parseInt(zip.value), 
                    activities: activities.value
                }
            );
            locationsDiv.innerHTML += `<div class="box" value='` + name.value + `'>` + name.value + 
            `<i class="material-icons" onclick="deleteLocation(this)">close</i></div>`;
            name.value = '';
            town.value = '';
            subdivision.value = '';
            country.value = '';
            zip.value = '';
            activities.value = [];
            activities.innerHTML = '';
        }
        function addActivity(){
            var activities = document.getElementById("activities");
            var name = document.getElementsByName("activity-name")[0];
            var start = document.getElementsByName("start")[0];
            var end = document.getElementsByName("end")[0];
            var p = participants.value;
            activities.value.push(
                {name: name.value, start: start.value, end: end.value, participants: p}
            );
            activities.innerHTML +=  `<div class="box" value='` + name.value + `'>` + name.value + 
            `<i class="material-icons" onclick="deleteActivity(this)">close</i></div>`;
            name.value = '';
            start.value = '';
            start.value = '';
            participants.value = [];
            participants.innerHTML = '';
        }
        function addLocations(){
            var locs = document.getElementsByName("locations")[0];
            locs.value = JSON.stringify(locations);
        }
        function addParticipant(e){
            if(e.keyCode == 188){
                var text = participantsForm.value.slice(0, participantsForm.value.length - 1).trim();
                var id = Array.from(document.getElementById("user-ids").childNodes).filter(function(elem){
                    return elem.getAttribute("name").toLowerCase().trim() == text;
                })[0];
                participantsForm.value = '';
                if(!id){
                    text += ' -<span style="color: red"> invalid user</span>';
                }else{
                    participants.value = participants.value.concat([{id: id, name: text}]);
                }
                participants.innerHTML += `<div class="box" value='` + text + `'>` + text + 
                `<i class="material-icons" onclick="deleteParticipant(this)">close</i></div>`;
            }
            return false;
        }
        function deleteParent(e){
            e.parentNode.parentNode.removeChild(e.parentNode);
        }
        function deleteParticipant(e){
            participants.value = participants.value.filter(function(elem){
                return elem.name != e.parentNode.getAttribute("value");
            });
            deleteParent(e);
        }
        function deleteActivity(e){
            activities.value = activities.value.filter(function(elem){
                return elem.name != e.parentNode.getAttribute("value");
            });
            deleteParent(e);
        }
        function deleteLocation(e){
            locations = locations.filter(function(elem){
                return elem.name != e.parentNode.getAttribute("value");
            });
            deleteParent(e);            
        }
        participantsForm.addEventListener('keyup', function(e){
            addParticipant(e);
        });
    </script>
<#include "partials/footer.ftlh">