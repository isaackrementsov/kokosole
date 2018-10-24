<#include "partials/header.ftlh">
    <#if trips?? && session.id??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <i class="material-icons notranslate" style="font-size: 14vw; color: #F76D6E">flight_takeoff</i>
                    <h1>Your trips</h1>
                </div>
                <div class="profile-bottom">
                    <div class="trip">
                        <#list trips as trip>
                            <div class="location"><a href="/trip/${trip.id}">${trip.name}</a></div>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="profile-right">
                <div class="profile-top">
                    <h1>Trips</h1>
                </div>
                <div class="profile-bottom">
                    <#list trips as trip>
                        <div class="location-card">
                            <h2>${trip.name}</h2>
                            <#list trip.locations as location>
                                <h3 style="display: inline">${location.town}, </h3>
                            </#list>
                            <div class="buttons">
                                <a class="navlink semi-cta outline" href="/trip/${trip.id}">View</a>
                            </div>
                        </div>
                    </#list>
                    <a class="location-card-add" href="/trip/add">
                        <i class="material-icons notranslate">
                            add
                        </i>
                    </a>
                    <h1>Shared Activities</h1>
                    <#list shared as activity>
                        <div class="location-card">
                            <h2>${activity.name}</h2>
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
                            <div class="buttons">
                                <button onclick="opendiv('participants${activity.id}', 'block')" class="navlink">Participants</button>
                            </div>
                            <div id="participants${activity.id}" style="display: none">
                                <#list activity.participants as participant>
                                    <div class="location">
                                        ${participant.name}
                                    </div>
                                </#list>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    <#else>
        <h1>No trips</h1>
    </#if>
<#include "partials/footer.ftlh">