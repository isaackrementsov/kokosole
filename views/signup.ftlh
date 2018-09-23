<#include "partials/header.ftlh">
    <div class="center main">
        <h1 class="important title light">Signup</h1>
        <h2 class="subtitle">Create your free account</h2>
        <div class="side form">
            <form class="register" method="post" action="/auth/signup">
                <label for="name">Name</label>
                <input type="text" placeholder="Jane Smith" name="name">
                <label for="email">Email</label>
                <input type="email" placeholder="jsmith@gmail.com" name="email">
                <label for="password">Password</label>
                <input type="password" placeholder="password123" name="password">
                <input type="submit" class="button cta no-shadow" value="Signup">
            </form>
        </div>
        <div class="side writing">
            <h3>Meet fellow explorers</h3>
            <i class="material-icons">near_me</i><p>Kokosole helps you meet up with people in places you intend to travel to.</p>
            <h3>Live chat</h3>
            <i class="material-icons">chat</i><p>Chat with those with whom you would like to meet in real-time.</p>
            <h3>Auto translation</h3>
            <i class="material-icons">translate</i><p>Easily communicate with international travellers with our auto-translation feature.</p>
        </div>
    </div>
<#include "partials/footer.ftlh">