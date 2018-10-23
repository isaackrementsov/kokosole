<#include "partials/header.ftlh">
    <form class="profile" action="/location/${tripID}" method="post" onsubmit="return addActivities()">
        <input type="hidden" name="activities">
        <div class="profile-left">
            <div class="profile-top">
                <h1>Add Location</h1>
            </div>
            <div class="profile-bottom">
                <label for="name" class="label">Town</label>
                <input type="text" name="town">
                <label for="name" class="label">Subdivision</label>
                <input type="text" name="subdivision">
                <label for="name" class="label">Country</label>
                <input type="text" name="country">
                <label for="name" class="label">Postal Code</label>
                <input type="number" name="zip" style="width: 79%; margin-bottom: 1vh">
                <input type="submit" class="smaller navlink cta" style="background-color: #F76D6E" value="Save location">
            </div>
        </div>
        <div class="profile-right">
            <div class="profile-top">
                <h1>Activities</h1>
                <div id="activities"></div>
            </div>
            <div class="profile-bottom">
                <div class="inline form-side" style="margin-right: 12%">
                    <h2 style="color:grey; font-size: 1.7vw; font-weight: 300">Participants</h2>
                    <div class="activity-form">
                        <label for="participants" class="label">Participant email</label>
                        <div id="participants"></div>
                        <input type="text" name="participants" placeholder="Separate emails with ','">              
                    </div>
                </div>
                <div class="inline form-side aligntop">
                    <label for="zip" class="label">Activity Name</label>
                    <input type="text" name="name" style="display: block; width: 100%">
                    <div class="inline" style="margin-right: 1.5vw">
                        <label for="country" class="label">Start</label>
                        <input type="text" name="start" placeholder="mm/dd/yyyy">
                    </div>
                    <div class="inline">
                        <label for="subdivision" class="label">End</label>
                        <input type="text" name="end" placeholder="mm/dd/yyyy">
                    </div>
                </div>
                <div class="profile-bottom">
                    <button type="button" class="smaller navlink cta" onclick="addActivity()" style="font-weight: 400; background-color: #FFCCCD; font-size: 1.05vw; margin-top: 3vh" >Save activity</button>
                </div>
            </div>
        </div>
    </form>
    <script>
        var activities = [];
        var activitiesDiv = document.getElementById("activities");
        var participantsForm = document.getElementsByName("participants")[0];
        var participants = document.getElementById("participants");
        participants.value = [];
        activitiesDiv.value = [];
        function addActivity(){
            var name = document.getElementsByName("name")[0];
            var start = document.getElementsByName("start")[0];
            var end = document.getElementsByName("end")[0];
            var p = participants.value;
            activities.push(
                {name: name.value, start: start.value, end: end.value, participants: p}
            );
            activitiesDiv.innerHTML +=  `<div class="box" value='` + name.value + `'>` + name.value + 
            `<i class="material-icons notranslate" onclick="deleteActivity(this)">close</i></div>`;
            name.value = '';
            start.value = '';
            end.value = '';
            participants.value = [];
            participants.innerHTML = '';
        }
        function addActivities(){
            var acts = document.getElementsByName("activities")[0];
            acts.value = JSON.stringify(activities);
        }
        function addParticipant(e){
            if(e.keyCode == 188){
                var text = participantsForm.value.slice(0, participantsForm.value.length - 1).trim().toLowerCase();
                participantsForm.value = '';
                participants.value.push(text);
                participants.innerHTML += `<div class="box" value='` + text + `'>` + text + 
                `<i class="material-icons notranslate" onclick="deleteParticipant(this)">close</i></div>`;
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
            activities = activities.filter(function(elem){
                return elem.name != e.parentNode.getAttribute("value");
            });
            deleteParent(e);
        }
        participantsForm.addEventListener('keyup', function(e){
            addParticipant(e);
        });
    </script>
<#include "partials/footer.ftlh">