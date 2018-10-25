<#include "partials/header.ftlh">
    <div class="profile">
        <div class="profile-left">
            <div class="profile-top">
                <img class="profile-picture" src="/public/${user.avatar}" onclick="opendiv('avatar', 'block')"> 
                <#if session.id??>
                    <#if session.id == user.id>
                        <form action="/user/${user.id}?_method=PATCH&key=avatar" method="post" id="avatar" style="display: none" enctype="multipart/form-data">
                            <label for="file">
                                <a class="navlink outline smaller no-shadow" style="display: inline-block" required>Change</a>
                                <input type="file" multiple name="avatar" style="display:none;" id="file">            
                            </label>
                            <input style="display: inline-block" type="submit" value="Save" class="navlink cta smaller no-shadow">
                        </form>
                    </#if>
                </#if> 
                <h2 style="color: #555; font-weight: 300">${user.email}</h2>
            </div>
            <h3>Trips</h3>
            <div class="profile-bottom" style="height: 30%; padding: 0;">
                <#if tripLen == 0>
                    <div class="trip">
                        <#if session.id??>
                            <#if session.id == user.id>
                                <h2>No trips yet</h2> 
                                <a class="cta navlink outline smaller" style="width: 4.4vw" href="/trip/add">Add one</a>
                            <#else>
                                <h2>No trips to show for ${user.name}</h2>                        
                            </#if>
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
                <h2 style="display: inline-block; margin-left: 1vw"><i class="material-icons notranslate">location_on</i>${user.country}</h2>
                <#assign color="green">
                <#if user.status == "inactive">
                    <#assign color="red">
                </#if>
                <h3 id="sts" onclick="opendivs([{id: 'updsts', display:'block'}, {id: 'sts', display:'block'}])">
                    <div class="circle" style="background-color: ${color}"></div>${user.status}
                </h3>
                <#if session.id??>
                    <#if session.id == user.id>
                        <form id="updsts" method="post" action="/user/${user.id}" style="display: none">
                            <input type="hidden" name="_method" value="PATCH">
                            <select name="status">
                                <option value="inactive">Inactive</option>
                                <option value="active">Active</option>
                            </select>
                            <input style="display: inline" type="submit" value="Save" class="navlink outline light">
                        </form>
                    </#if>
                </#if>
                <div class="stat"><h3>${locLen}</h3><h4>Locations</h4></div>
                <div class="stat"><h3>${tripLen}</h3><h4>Trips</h4></div><br>
                <#if session.id??>
                    <#if session.id != user.id>
                        <form method="post" action="/conversation/${session.id}" class="inline" style="padding:0;margin:0">
                            <input type="hidden" name="participants" value="['${user.email}']">
                            <input type="hidden" name="name" value="Conversation with ${user.name}">
                            <button class="inline shade" type="submit" style="padding:0 1vw;padding-top:1vh;background-color: transparent; border-style: hidden">
                                <h4 style="margin:0"><i class="material-icons notranslate">chat_bubble</i> Message</h4>
                            </button> 
                        </form>
                    </#if>
                </#if>
                <div class="shade inline" style="margin-left: 1vw;padding-top:1vh">${level}</div>
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