        </div>
    </body>
    <script>
        var div;
        function googleTranslateElementInit() {
            new google.translate.TranslateElement({pageLanguage: 'en'}, 'google_translate_element');
            div = document.getElementsByClassName('goog-te-combo')[0];
            if(div){
                div.addEventListener('input', function(){
                    opendiv('google_translate_element');
                });
            }
            setTimeout(lower, 1000);
        }
        function lower(){
            var navbar = document.getElementsByClassName('navbar')[0];
            var content = document.getElementsByClassName('content')[0];
            content.style.marginTop = navbar.offsetHeight + 'px';
        }
    </script>
    <script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
    <script src="/public/main.js"></script>
</html>