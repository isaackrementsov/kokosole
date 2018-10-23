<#include "partials/header.ftlh">
    <div class="profile">
        <div class="profile-left">
            <div class="profile-top">
                <i class="material-icons notranslate" style="font-size: 14vw; color: #F76D6E">chat_bubble_outline</i>
                <h1>
                    Conversations
                </h1>
            </div>
            <div class="profile-bottom">
                <div class="trip">
                    <h2>Your conversations</h2>
                    <#list conversations as conversation>
                        <a class="location" href="/conversation/${conversation.id}">${conversation.title}</a>
                    </#list>
                </div>
                <div class="trip">
                    <h2>Shared</h2>
                    <#list sharedConversations as conversation>
                        <a class="location" href="/conversation/${conversation.id}">${conversation.title}</a>
                    </#list>
                </div>
            </div>
        </div>
        <div class="profile-right">
            <div class="profile-top">
                <h1>Conversations</h1>
            </div>
            <div class="profile-bottom">
                <table class="list">
                    <#list conversations as conversation>
                        <tr>
                            <td class="title">
                                <span>
                                    ${conversation.title}
                                </span>
                            </td>
                            <td class="participants">
                                <#list conversation.participants as participant>
                                    <span class="location">${participant.name}</span>
                                </#list>
                            </td>
                            <td class="view"><a class="navlink cta outline" href="/conversation/${conversation.id}">View</a></td>
                        </tr>
                    </#list>
                    <#list sharedConversations as conversation>
                    <tr>
                        <td class="title">
                            <span>
                                ${conversation.title}
                            </span>
                        </td>
                        <td class="participants">
                            <#list conversation.participants as participant>
                                <span class="location">${participant.name}</span>
                            </#list>
                        </td>
                        <td class="view"><a class="navlink cta outline" href="/conversation/${conversation.id}">View</a></td>
                    </tr>
                </#list>
                    <tr id="plus-activity">
                        <td colspan="100%" class="add">
                            <i class="material-icons notranslate" onclick="opendivs([{display: 'table-row', id: 'plus-activity'}, {display: 'inline-block', id: 'add-activity'}])">
                                add
                            </i>
                        </td>
                    </tr>
                </table>
                <div class="location-card location-card-new" id="add-activity" style="display: none">
                    <form method="post" action="/conversation/${session.id}" class="activity-form" style="padding: 0; border-color: transparent" onsubmit="addParticipants()">
                        <label for="name" class="label">Chat name</label>
                        <input type="text" name="name">    
                        <label for="participants" class="label">Participants</label>
                        <div id="participants"></div>
                        <input type="text" name="participants" placeholder="Separate emails with ','">
                        <div class="buttons">
                            <button type="button" onclick="opendivs([{display: 'inline-block', id: 'add-activity'}, {display: 'table-row', id: 'plus-activity'}])" class="navlink">Cancel</button>
                            <input type="submit" value="Save" class="navlink cta outline">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
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
                participantsForm.value = '';
                participants.push(text);
                participantsDiv.innerHTML += `<div class="box" value="` + text + `">` + text + 
                `<i class="material-icons notranslate" onclick="deleteParticipant(this)">close</i></div>`;
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
<#include "partials/footer.ftlh">