<#include "partials/header.ftlh">
    <div class="profile">
        <div class="profile-left">
            <div class="profile-top">
                <img class="profile-picture" src="/public/${user.avatar}" onclick="opendiv('avatar', 'block')"> 
                <#if session.id??>
                    <#if session.id == user.id>
                        <form action="/user/?_method=PATCH&key=avatar" method="post" id="avatar" style="display: none" enctype="multipart/form-data">
                            <label for="file">
                                <a class="navlink outline smaller no-shadow" style="display: inline-block" required>Change</a>
                                <input type="file" name="avatar" style="display:none;" id="file">            
                            </label>
                            <input style="display: inline-block" type="submit" value="Save" class="navlink cta smaller no-shadow">
                        </form>
                    </#if>
                </#if> 
            </div>
            <h3>Trips</h3>
            <div class="profile-bottom" style="height: 30%; padding: 0;">
                <#if tripLen == 0>
                    <div class="trip">
                        <#if session.id??>
                            <h2>You have no trips yet</h2> 
                            <a class="cta navlink outline smaller" style="width: 4.4vw" href="/trip/add">Add one</a>
                        <#else>
                            <h2>No trips to show for ${user.name}</h2>                        
                        </#if>
                    </div>
                <#else>
                    <#list trips as trip>
                        <div class="trip">
                            <h2><a href="/trip/${trip.id}">${trip.name}</a></h2>
                            <#list trip.locations as location>
                                <div class="location">${location.town}</div>
                            </#list>
                        </div>  
                    </#list>
                </#if>
            </div>
        </div>
        <div class="profile-right">
            <div class="profile-top">
                <h1 style="display: inline-block; vertical-align: center">${user.name}</h1>
                <h2 style="display: inline-block; margin-left: 1vw"><i class="material-icons">location_on</i>${user.country}</h2>
                <#assign color="green">
                <#if user.status == "inactive">
                    <#assign color="red">
                </#if>
                <h3><div class="circle" style="background-color: ${color}"></div>${user.status}</h3>
                <div class="stat"><h3>${locLen}</h3><h4>Locations</h4></div>
                <div class="stat"><h3>${tripLen}</h3><h4>Trips</h4></div><br>
                <a class="inline" href="/messages?start-with=${user.id}">
                    <h4><i class="material-icons">chat_bubble</i> Message</h4>
                </a> 
                <div class="shade inline" style="margin-left: 1vw">Beginner user</div>
            </div>
            <div class="profile-bottom">
                <#if session.id??>
                    <#if session.id == user.id>
                        <form action="/user/null" method="post">
                            <input type="hidden" name="_method" value="PATCH">
                            <#if user.bio == ''>
                                <p id="bio" onclick="opendivs([{id: 'textarea', display: 'block'}, {id: 'edit', display: 'block'}, {id: 'bio', display: 'block'}])">
                                    No bio yet
                                </p>
                            <#else>
                                <p onclick="opendivs([{id: 'textarea', display: 'block'}, {id: 'edit', display: 'block'}, {id: 'bio', display: 'block'}])" id="bio">${user.bio}</p>
                            </#if>
                            <textarea name="bio" id="textarea" style="display: none">${user.bio}</textarea>
                            <input type="submit" class="outline cta button smaller no-shadow" value="Save" style="display:none" id="edit">
                        </form>
                    <#else>
                        <#if user.bio == ''>
                            <p id="bio">
                                No bio yet
                            </p>
                        <#else>
                            <p id="bio">${user.bio}</p>
                        </#if>
                    </#if>
                <#else>
                <#if user.bio == ''>
                    <p id="bio">
                        No bio yet
                    </p>
                    <#else>
                        <p id="bio">${user.bio}</p>
                    </#if>
                </#if>
            </div>
        </div>
    </div>
<#include "partials/footer.ftlh">