<#include "partials/header.ftlh">
    <#if trip??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <i class="material-icons" style="font-size: 14vw; color: #F76D6E">terrain</i>
                    <h1>
                        <span id="name" onclick="opendivs([{display: 'inline-block', id: 'upd'}, {display: 'inline', id: 'name'}])">${trip.name}</span>
                        <#if session.id??>
                            <form id="upd" style="display: none" method="post" action="/trip/${trip.id}">
                                <input type="hidden" name="_method" value="PATCH">
                                <input type="text" name="name" value="${trip.name}">
                            </form>
                        </#if>
                    </h1>
                </div>
                <div class="profile-bottom">
                    <div class="trip">
                        <#list trip.locations as location>
                            <div class="location"><a href="/location/${location.id}">${location.town}</a></div>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="profile-right">
                <div class="profile-top">
                    <h1>Locations</h1>
                </div>
                <div class="profile-bottom">
                    <#list trip.locations as location>
                        <div class="location-card">
                            <h2>${location.town}</h2>
                            <h3>${location.subdivision}</h3>
                            <h3>${location.country}</h3>
                            <div class="buttons">
                                <button onclick="opendiv('activities${location.id}', 'block')" class="navlink">Activities</button>
                                <a href="/location/${location.id}" class="navlink cta outline">View</a>
                            </div>
                            <div id="activities${location.id}" style="display: none">
                                <#list location.activities as activity>
                                    <div class="location">${activity.name}</div>
                                </#list>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    <#else>
        <h1>Trip not found</h1>
    </#if>
<#include "partials/footer.ftlh">