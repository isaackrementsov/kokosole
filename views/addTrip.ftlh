<#include "partials/header.ftlh">
    <form class="profile" action="/trip/add" method="post" onsubmit="return addLocations()">
        <input type="hidden" name="locations">
        <div class="profile-left">
            <div class="profile-top">
                <i class="material-icons notranslate" style="font-size: 14vw; color: #F76D6E">terrain</i>
                <h1>Add Trip</h1>
            </div>
            <div class="profile-bottom">
                <label for="name" class="label">Name</label>
                <input type="text" required name="name">
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
                        <label for="activity-name" class="label">Activity name</label>
                        <input type="text" name="activity-name"> 
                        <div class="dates">
                            <label for="start" class="label">Dates (mm/dd/yyyy)</label>
                            <input type="text" class="inline" name="start" placeholder="Start">  
                            <input type="text" class="inline" name="end" placeholder="End">
                        </div>    
                        <label for="participants-name" class="label">Participants</label>
                        <div id="participants"></div>
                        <input type="email" name="participants" placeholder="Separate emails with ','">
                        <button type="button" class="smaller navlink" style="font-weight: 400; background-color: #FFCCCD; font-size: 1.05vw; margin-top: 3vh" onclick="addActivity()">
                            Save activity
                        </button>                
                    </div>
                </div>
                <div class="inline form-side aligntop">
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="town" class="label">City</label>
                        <input type="text" name="town">
                    </div>
                    <div class="inline">
                        <label for="zip" class="label">Postal Code</label>
                        <input type="number" name="zip">
                    </div>
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="country" class="label">Country</label>
                        <input type="text" name="country">
                    </div>
                    <div class="inline">
                        <label for="subdivision" class="label">Subdivision</label>
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
            var town = document.getElementsByName("town")[0];
            var subdivision = document.getElementsByName("subdivision")[0];
            var country = document.getElementsByName("country")[0];
            var zip = document.getElementsByName("zip")[0];
            if(town.value && subdivision.value && country.value && zip.value){
                locations.push(
                    {
                        town: town.value, 
                        subdivision: subdivision.value, 
                        country: country.value, 
                        zip: parseInt(zip.value), 
                        activities: activities.value
                    }
                );
                locationsDiv.innerHTML += `<div class="box" value='` + town.value + `'>` + town.value + 
                `<i class="material-icons notranslate" onclick="deleteLocation(this)">close</i></div>`;
                name.value = '';
                town.value = '';
                subdivision.value = '';
                country.value = '';
                zip.value = '';
                activities.value = [];
                activities.innerHTML = '';
            }
        }
        function addActivity(){
            var activities = document.getElementById("activities");
            var name = document.getElementsByName("activity-name")[0];
            var start = document.getElementsByName("start")[0];
            var end = document.getElementsByName("end")[0];
            var p = participants.value;
            if(name.value && start.value && end.value){
                activities.value.push(
                    {name: name.value, start: start.value, end: end.value, participants: p}
                );
                activities.innerHTML +=  `<div class="box" value='` + name.value + `'>` + name.value + 
                `<i class="material-icons notranslate" onclick="deleteActivity(this)">close</i></div>`;
                name.value = '';
                start.value = '';
                end.value = '';
                participants.value = [];
                participants.innerHTML = '';
            }
        }
        function addLocations(){
            var locs = document.getElementsByName("locations")[0];
            locs.value = JSON.stringify(locations);
        }
        function addParticipant(e){
            if(e.keyCode == 188){
                var text = participantsForm.value.slice(0, participantsForm.value.length - 1).trim().toLowerCase();
                if(text){
                    participants.value.push(text);
                    participants.innerHTML += `<div class="box" value='` + text + `'>` + text + 
                    `<i class="material-icons notranslate" onclick="deleteParticipant(this)">close</i></div>`;
                }
                participantsForm.value = '';
            }
            return false;
        }
        function deleteParent(e){
            e.parentNode.parentNode.removeChild(e.parentNode);
        }
        function deleteParticipant(e){
            participants.value = participants.value.filter(function(elem){
                return elem != e.parentNode.getAttribute("value");
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
                return elem.town != e.parentNode.getAttribute("value");
            });
            deleteParent(e);            
        }
        participantsForm.addEventListener('keyup', function(e){
            addParticipant(e);
        });
    </script>
<#include "partials/footer.ftlh">