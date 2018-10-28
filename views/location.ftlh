<#include "partials/header.ftlh">
    <#if location.town??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <i class="material-icons notranslate" style="font-size: 14vw; color: #F76D6E">location_city</i>
                    <h1>
                        <span id="name" onclick="opendivs([{display: 'inline-block', id: 'upd'}, {display: 'inline', id: 'name'}])">${location.town}</span>
                        <#if permission>
                            <form id="upd" style="display: none" method="post" action="/location/${location.id}">
                                <input type="hidden" name="_method" value="PATCH">
                                <input type="text" required name="town" value="${location.town}">
                            </form>
                        </#if>
                    </h1>
                </div>
                <div class="profile-bottom">
                    <div class="trip">
                        <#list location.activities as activity>
                            <div class="location">${activity.name}</div>
                        </#list>
                    </div>
                    <form method="post" action="/location/${location.id}" class="trip">
                        <input type="hidden" name="_method" value="PATCH">
                        <div id="infos" onclick="opendivs([{display: 'block', id: 'info'}, {display: 'block', id: 'infos'}])">
                            <h2>${location.subdivision}</h2>
                            <h2>${location.country}</h2>
                        </div>
                        <div id="info" style="display: none">
                            <input type="text" required name="subdivision" value="${location.subdivision}">
                            <input type="text" required name="country" value="${location.country}">
                            <input type="submit" value="Save" class="navlink outline dte-button">
                        </div>
                        <#if permission>
                            <form method="post" action="/location/${location.id}">
                                <input type="hidden" name="_method" value="DELETE">
                                <input style="margin-top: 3vh" type="submit" value="Delete" class="smaller navlink cta normal">
                            </form>
                        </#if>
                    </form>
                </div>
            </div>
            <div class="profile-right">
                <div class="profile-top">
                    <h1>Activities</h1>
                </div>
                <div class="profile-bottom">
                    <#list location.activities as activity>
                        <div class="location-card">
                            <h2 id="nme" onclick="opendivs([{display: 'block', id: 'nme'}, {display: 'block', id: 'updnme'}])">
                                <#if permission>
                                    <form method="post" action="/activity/${activity.id}" style="display: inline; vertical-align: 10%">
                                        <input type="hidden" name="_method" value="DELETE">
                                        <input type="submit" value="x " class="blank">
                                    </form>
                                </#if>
                                 ${activity.name}
                            </h2>
                            <form method="post" action="/activity/${activity.id}" style="display: none;" id="updnme">
                                <input type="hidden" name="_method" value="PATCH">
                                <input type="text" name="name" value="${activity.name}">
                            </form>
                            <form method="post" action="/activity/${activity.id}">
                                <input type="hidden" name="_method" value="PATCH">
                                <#assign start = activity.duration.start>
                                <#assign startDate = start.month + " " + start.dayOfMonth + " " + start.year?c>
                                <#assign startSlash = start.monthValue + "/" + start.dayOfMonth + "/" + start.year?c>
                                <#assign end = activity.duration.end>
                                <#assign endDate = end.month + " " + end.dayOfMonth + " " + end.year?c>
                                <#assign endSlash = end.monthValue + "/" + end.dayOfMonth + "/" + end.year?c>
                                <div id="current-dates" onclick="opendivs([{display: 'block', id: 'dates'}, {display: 'block', id: 'current-dates'}])">
                                    <#if startDate == endDate>
                                        <h3 class="date">ON ${startDate}</h3>
                                    <#else>
                                        <h3 class="date">STARTS ${startDate}</h3>
                                        <h3 class="date">ENDS ${endDate}</h3>                           
                                    </#if>
                                </div>
                                <#if permission>
                                    <div id="dates" style="display: none">
                                        <input class="dte" type="text" required value="${startSlash}" name="start">
                                        <input class="dte" type="text" required value="${endSlash}" name="end">
                                        <input type="submit" value="Save" class="navlink outline dte-button">
                                    </div>
                                </#if>
                            </form>
                            <div class="buttons">
                                <button onclick="opendiv('participants${activity.id}', 'block')" class="navlink">Participants</button>
                            </div>
                            <div id="participants${activity.id}" style="display: none">
                                <#list activity.participants as participant>
                                    <div class="location">
                                        ${participant.name}
                                        <#if permission>
                                            <form method="post" action="/activity/${activity.id}?action=participants&sub=delete" style="display: inline" class="blank">
                                                <input type="hidden" name="email" value="${participant.email}">
                                                <input type="hidden" name="_method" value="PATCH">
                                                <input type="submit" value=" x" class="blank">
                                            </form>
                                        </#if>
                                    </div>
                                </#list>
                                <#if permission>
                                    <div class="location" id="add" onclick="opendivs([{display: 'inline-flex', id: 'close'}, {display: 'inline-flex', id: 'add'}, {display: 'block', id: 'newparticipant'}])">+</div>
                                    <div class="location" id="close" style="display: none;" onclick="opendivs([{display: 'block', id: 'newparticipant'}, {display: 'inline-flex', id: 'close'}, {display: 'inline-flex', id: 'add'}])">x</div>
                                    <form id="newparticipant" method="post" action="/activity/${activity.id}?action=participants&sub=add" style="display: none">
                                        <input type="hidden" name="_method" value="PATCH">
                                        <input type="email" placeholder="Email" name="email">
                                        <input type="submit" value="Save" class="navlink outline dte-button">
                                    </form>
                                </#if>
                            </div>
                        </div>
                    </#list>
                    <#if permission>
                        <div class="location-card-add"  id="plus-activity">
                            <i class="material-icons notranslate" onclick="opendivs([{display: 'inline-block', id: 'plus-activity'}, {display: 'inline-block', id: 'add-activity'}])">
                                add
                            </i>
                        </div>
                        <div class="location-card location-card-new" id="add-activity" style="display: none">
                            <form method="post" action="/activity/${location.id}" class="activity-form" style="padding: 0; border-color: transparent" onsubmit="addParticipants()">
                                <label for="activity-name" class="label">Activity name</label>
                                <input type="text" name="name"> 
                                <div class="dates">
                                    <label for="start" class="label">Dates (mm/dd/yyyy)</label>
                                    <input type="text" class="inline" name="start" placeholder="Start">  
                                    <input type="text" class="inline" name="end" placeholder="End">
                                </div>    
                                <label for="participants-name" class="label">Participants</label>
                                <div id="participants"></div>
                                <input type="text" name="participants" placeholder="Separate emails with ','">
                                <div class="buttons">
                                    <button type="button" onclick="opendivs([{display: 'inline-block', id: 'add-activity'}, {display: 'inline-block', id: 'plus-activity'}])" class="navlink">Cancel</button>
                                    <input type="submit" value="Save" class="navlink cta outline">
                                </div>
                            </form>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
        <script>
            var ds = document.getElementsByClassName("dte");
            for(let i = 0; i < ds.length; i++){
                let slashes = ds[i].value.split("/");
                slashes = slashes.map(function(elem){
                    if(elem.length == 1){
                        elem = "0" + elem;
                    }
                    return elem;
                });
                ds[i].value = slashes[0] + "/" + slashes[1] + "/" + slashes[2];
            }
        </script>
        <script>
            var participants = [];
            var participantsForm = document.getElementsByName("participants")[0];
            var participantsDiv = document.getElementById("participants");
            function addParticipants(){
                participantsForm.value = JSON.stringify(participants);
            }
            function addParticipant(e){
                if(e.keyCode == 188){
                    var text = participantsForm.value.slice(0, participantsForm.value.length - 1).trim().toLowerCase();
                    if(text){
                        participantsForm.value = '';
                        participants.push(text);
                        participantsDiv.innerHTML += `<div class="box" value="` + text + `">` + text + 
                        `<i class="material-icons notranslate" onclick="deleteParticipant(this)">close</i></div>`;
                    }
                }
                return false;
            }
            function deleteParent(e){
                e.parentNode.parentNode.removeChild(e.parentNode);
            }
            function deleteParticipant(e){
                participants = participants.filter(function(elem){
                    return elem != e.parentNode.getAttribute("value");
                });
                deleteParent(e);
            }
            participantsForm.addEventListener('keyup', function(e){
                addParticipant(e);
            });
        </script>
    <#else>
        <h1>Location not found</h1>
    </#if>
<#include "partials/footer.ftlh">