<#include "partials/header.ftlh">
    <#if conversation.title??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <h1 id="ttl" onclick="opendivs([{id: 'ttlupd', display: 'block'}, {id: 'ttl', display: 'block'}])">${conversation.title}</h1>
                    <#if session.id == conversation.userID>
                        <form id="ttlupd" method="post" action="/conversation/${conversation.id}" style="display:none">
                            <input type="hidden" name="_method" value="PATCH">
                            <input type="text" name="title" value="${conversation.title}">
                        </form>
                    </#if>
                    <#if session.id == conversation.userID>
                        <form method="post" action="/conversation/${conversation.id}">
                            <input type="hidden" name="_method" value="DELETE">
                            <input style="margin-top: 3vh" type="submit" value="Delete" class="smaller navlink cta normal">
                        </form>
                    </#if>
                    <#if session.id == conversation.userID>
                        <div class="trip" style="border-top-style: solid; border-top-color: lightgrey; border-width: 1px">
                            <h2>Add a user</h2>
                            <form class="in" method="post" action="/conversation/${conversation.id}?action=participants&sub=add" style="margin-bottom: 0">
                                <input type="hidden" name="_method" value="PATCH">
                                <input type="text" name="email" placeholder="Email">
                                <input type="submit" value="Add" class="outline navlink smaller light">
                            </form>
                        </div>
                    </#if>
                </div>
                <div class="profile-bottom" style="max-height: 30vh; overflow: auto">
                    <#list conversation.participants as participant>
                        <div class="trip part">
                            <div class="av">
                                <img class="avatar" src="/public/${participant.avatar}">
                            </div>
                            <a class="in" href="/user/${participant.id}">
                                <h2>
                                    ${participant.name}
                                    <#if session.id == conversation.userID && participant.id != conversation.userID>
                                        <form method="post" action="/conversation/${conversation.id}?action=participants&sub=delete" style="display: inline; vertical-align: 10%">
                                            <input type="hidden" name="_method" value="PATCH">
                                            <input type="hidden" name="email" value="${participant.email}">
                                            <input type="submit" value="x " class="blank">
                                        </form>
                                    </#if>
                                </h2>
                                <h3>${participant.email}</h3>
                            </a>
                        </div>
                    </#list>
                </div>
            </div>
            <div class="profile-right">
                <div class="profile-top">
                    <h1>Messages</h1>
                </div>
                <div class="profile-bottom">
                    <div class="messages">
                        <#list messages as message>
                            <#assign classes = "message ">
                            <#if session.email == message.userEmail>
                                <#assign classes += "your">
                            </#if>
                            <p class="${classes}">
                                ${message.content}<br>
                                <span class="timestamp">
                                    <#assign auth = message.userEmail>
                                    <#if auth == session.email>
                                        <#assign auth = "You">
                                    </#if>
                                    ${auth}
                                    <#assign s = message.sentAt>
                                    <#assign date = s.monthValue + "-" + s.dayOfMonth + "-" + s.year?c + "-" + s.hour + ":" + s.minute + ":" + s.second>
                                    <span class="timeAgo" id="${date}">
                                        ${date}
                                    </span>
                                </span>
                            </p>
                        </#list>
                    </div>
                    <div class="message-send">
                        <input type="text" id="message" style="float: left; width: dsd">
                        <button class="navlink cta outline" onclick="sendMsg()" style="margin-top: 0">Send</button>
                    </div>
                </div>
            </div>
        </div>
        <script>
            var cookie = document.cookie.split(';')[0].split("=")[1];
            var ws = new WebSocket('ws://localhost:8000/' + cookie);
            var chat = document.getElementsByClassName("messages")[0];
            var input = document.getElementById("message");
            var dates = document.getElementsByClassName("timeAgo");
            chat.scrollTop = chat.offsetHeight + 1000;
            Date.prototype.timeAgo = function(){
                var seconds = ((new Date() - this) / 1000);
                var interval = (seconds / 31536000);
                if(interval > 1) return Math.floor(interval) + ' years';
                interval = Math.floor(seconds / 2592000);
                if(interval > 1) return Math.floor(interval) + ' months';
                interval = (seconds / 86400);
                if(interval > 1) return Math.floor(interval) + 'd';
                interval = (seconds / 3600);
                if(interval > 1) return Math.floor(interval) + 'h';
                interval = (seconds / 60);
                if(interval > 1) return Math.floor(interval) + 'm';
                return Math.floor(seconds) + 's';
            }
            function updTime(){
                for(let i = 0; i < dates.length; i++){
                    var date = new Date(dates[i].id).timeAgo();
                    if(date[date.length - 1] == 's'){
                        date = 'now';
                    }
                    dates[i].innerHTML = date;
                }
            }
            updTime();
            setInterval(updTime, 10000);
            input.addEventListener('keyup', function(e){
                if(e.keyCode == 13){
                    sendMsg();
                }
            });
            function sendMsg(){
                var val = input.value;
                input.value = '';
                if(ws.readyState == WebSocket.OPEN){
                    ws.send(val);
                }else{
                    throw 'No connection';
                }
            }
            ws.addEventListener('message', function(msg){
                var parts = msg.data.split("|");
                var timeAgo = parts[2];
                var author = parts[1];
                var message = parts[0];
                var classes = 'message ';
                var date = new Date(timeAgo).timeAgo();
                if(date[date.length - 1] == 's'){
                    date = 'now';
                }
                if(author == `${session.email}`){
                    classes += 'your';
                    author = 'You';
                }       
                chat.innerHTML += `<p class="` + classes + `">` 
                        + message + 
                        `<br><span class="timestamp">`
                             + author + 
                            `<span class="timeAgo" id=` + timeAgo + `>` + date + `</span>
                        </span>
                    </p>`;
                chat.scrollTop = chat.offsetHeight + 1000;
            });
        </script>
    <#else>
        <h1>Conversation not found</h1>
    </#if>
<#include "partials/footer.ftlh">