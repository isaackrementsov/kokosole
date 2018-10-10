<#include "partials/header.ftlh">
    <#if trips?? && session.id??>
        <div class="profile">
            <div class="profile-left">
                <div class="profile-top">
                    <i class="material-icons" style="font-size: 14vw; color: #F76D6E">flight_takeoff</i>
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
                                <a class="navlink cta outline" href="/trip/${trip.id}">View</a>
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