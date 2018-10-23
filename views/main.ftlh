<#include "partials/header.ftlh">
    <style>
        body, html {
            background: linear-gradient(115deg, #e66465, #9198e5);
            color: white;
        }
        .navbar {
            border-style: hidden;
        }
    </style>
    <div class="side">
        <#include "partials/location.ftlh">
    </div>
    <div class="info side">
        <h1 class="important">Make Your Next Trip More Interesting</h1>
        <p>Whether you want to climb a mountain in Bora-Bora or go out to eat in Venice, 
            Kokosole can make sure that travelling women can meet with fellow explorers around the world.</p>
        <a class="button white" href="/auth/signup">GET STARTED</a>
    </div>
    <div id="google_translate_element">
        Would you like to translate this page?
        <button class="blank" onclick="opendiv('google_translate_element')" id="close">No thanks</button>
    </div>
    <script>
        var div;
        function googleTranslateElementInit() {
            new google.translate.TranslateElement({pageLanguage: 'en'}, 'google_translate_element');
            div = document.getElementsByClassName('goog-te-combo')[0];
            div.addEventListener('input', function(){
                opendiv('google_translate_element');
            });
        }
    </script>
<#include "partials/footer.ftlh">