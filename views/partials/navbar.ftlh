<ul class="navbar">
    <#if session.name??>
        <li class="left"><#include "logo.ftlh"></li>
        <li class="left"><h1><a href="/">Kokosole</a></h1></li> 
        <li><a href="/user/${session.id}" class="alink navlink">
            <img src="/public/${session.avatar}" class="avatar">
        </a></li>
        <li>
            <form method="post" action="/auth/logout" style="padding: 0; margin: 0">
                <input type="submit" class="navlink" value="Logout" name="logout">
            </form>
        </li>
        <li><a href="/trip/add" class="outline cta navlink"><i class="material-icons">add</i> Add trip</a></li>
        <li><a href="/trip/home" class="navlink">Trips</a></li>              
        <li><a href="/message/home" class="navlink">Messages</a></li>           
    <#else>
        <li class="left"><#include "logo.ftlh"></li>
        <li class="left"><h1><a href="/">Kokosole</a></h1></li>
        <li><a href="/auth/signup" class="outline cta navlink">Signup</a></li>
        <li><a href="/auth/login" class="outline navlink">Login</a></li>
    </#if>
</ul>