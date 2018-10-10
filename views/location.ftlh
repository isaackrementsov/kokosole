<#include "partials/header.ftlh">
    <#if location??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <i class="material-icons" style="font-size: 14vw; color: #F76D6E">location_city</i>
                    <h1>
                        <span id="name" onclick="opendivs([{display: 'inline-block', id: 'upd'}, {display: 'inline', id: 'name'}])">${location.town}</span>
                        <#if session.id??>
                            <form id="upd" style="display: none" method="post" action="/location/${location.id}">
                                <input type="hidden" name="_method" value="PATCH">
                                <input type="text" name="town" value="${location.town}">
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
                    <div class="trip">
                        <h2>${location.subdivision}</h2>
                        <h2>${location.country}</h2>
                    </div>
                </div>
            </div>
            <div class="profile-right">
                <div class="profile-top">
                    <h1>Activities</h1>
                </div>
                <div class="profile-bottom">
                    <#list location.activities as activity>
                        <div class="location-card">
                            <form method="post" action="/activity/${activity.id}">
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
                                <#if session.id??>
                                    <div id="dates" style="display: none">
                                        <input type="text" value="${startSlash}" name="start">
                                        <input type="text" value="${endSlash}" placeholder="End" name="end">
                                    </div>
                                </#if>
                            </form>
                            <div class="buttons">
                                <button onclick="opendiv('participants${activity.id}', 'block')" class="navlink">Participants</button>
                                <a href="/messages/${activity.id}" class="navlink cta outline">Message</a>
                            </div>
                            <div id="participants${activity.id}" style="display: none">
                                <#list activity.participants as participant>
                                    <div class="location">${participant.name}</div>
                                </#list>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    <#else>
        <h1>Location not found</h1>
    </#if>
<#include "partials/footer.ftlh">