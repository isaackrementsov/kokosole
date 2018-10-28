<#include "partials/header.ftlh">
    <style>
        body, html {
            background-color: whitesmoke;
        }
        .navbar {
            border-style: hidden;
        }
    </style>
    <div class="center main">
        <h1 class="important title light">Login</h1>
        <h2 class="subtitle">Access your account</h2>
        <div class="side form" style="display:block; width: 100%;">
            <form class="login" method="post" action="/auth/login" style="margin-left: auto; margin-right: auto; padding-top: 1vh;">
                <label for="email">Email</label>
                <input type="email" required name="email">
                <label for="password">Password</label>
                <input type="password" required required name="password">
                <a class="button outline smaller no-shadow lato" href="/auth/signup" style="display:inline-block; margin-left: 0; margin-top: 4vh; margin-bottom: 2vh;">Signup</a>
                <button type="submit" class="button cta smaller no-shadow lato" style="display:inline-block;float: right; margin-top: 4vh;margin-bottom: 2vh;">Login</button>
            </form>
        </div>
    </div>
<#include "partials/footer.ftlh">