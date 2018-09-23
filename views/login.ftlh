<#include "partials/header.ftlh">
    <div class="center main">
        <h1 class="important title light">Login</h1>
        <h2 class="subtitle">Access your account</h2>
        <div class="side form" style="display:block; width: 100%;">
            <form class="login" method="post" action="/auth/login" style="margin-left: auto; margin-right: auto; padding-top: 1vh;">
                <label for="email">Email</label>
                <input type="email" placeholder="jsmith@gmail.com" name="email">
                <label for="password">Password</label>
                <input type="password" placeholder="password123" name="password">
                <a class="button outline smaller no-shadow" href="/auth/signup" style="display:inline-block; margin-left: 0; margin-top: 4vh; margin-bottom: 2vh;">Signup</a>
                <input type="submit" class="button cta smaller no-shadow" value="Login" style="display:inline-block;float: right; margin-top: 4vh;margin-bottom: 2vh;">
            </form>
        </div>
    </div>
<#include "partials/footer.ftlh">