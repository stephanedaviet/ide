<!--

    CODENVY CONFIDENTIAL
    __________________

    [2012] - [2013] Codenvy, S.A.
    All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Codenvy S.A. and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Codenvy S.A.
    and its suppliers and may be covered by U.S. and Foreign Patents,
    patents in process, and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Codenvy S.A..

-->

<%!
	public String staticResourceUrl(HttpServletRequest request, String name) {
	   return request.getContextPath() + "/" + request.getAttribute("ws") + "/_app/" + name;
	}
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    
    <title>IDE</title>
    
    <script type="text/javascript">
		var ide_base_path = '<%=request.getContextPath() + "/" + request.getAttribute("ws") + "/_app/"%>';

        var appConfig = {
            "context": "/ide/rest/",
            "websocketContext": "/ide/websocket/"
        };
        
        // URL to refresh SSO before creation a Factory.
        // var ssoInitURL = "/sso-init-url";
        
        // URL to html with Google like button
        var google_like_url = ide_base_path + "google-like.html";

        // URL to html with Facebook like button
		var facebook_like_url = ide_base_path + "facebook-like.html";
        
        var hiddenFiles = ".*";
        var ws = "<%= request.getAttribute("ws")%>";
        var project = <%= request.getAttribute("project") != null ? "\"" + request.getAttribute("project")  + "\"" : null%>;
        var path = <%= request.getAttribute("path") != null ? "\"" + request.getAttribute("path")  + "\"" : null%>;
        var startUpParams = <%= request.getAttribute("startUpParams") != null ? "\"?" + request.getAttribute("startUpParams")  + "\"" : null%>;
        var authorizationContext = "/api";
        
        var authorizationErrorPageURL = "/ide/ide/error_oauth.html";
        
        // URL to checking security
        var securityCheckURL = "/ide/j_security_check";
    </script>

    <link rel="shortcut icon" href="/images/favicon.ico"/>

    <script type="text/javascript" language="javascript" src='<%= staticResourceUrl(request, "greeting.js")%>'></script>

<!-- 
    <link type="text/css" rel="stylesheet" href='<%= staticResourceUrl(request, "top-menu.css")%>' media="all"/>
 -->
    
<!-- 
    <link href='<%= staticResourceUrl(request, "css/ide01.css")%>' media="screen" rel="stylesheet" type="text/css"/>
 -->
    
</head>

<body>

<script type="text/javascript" language="javascript" src='<%= staticResourceUrl(request, "browserNotSupported.js")%>'></script>

<!-- Deprecated -->
<div id="ide-preloader" style="position: absolute; left:0px; right: 0px; top:0px; bottom: 0px; background-color: #FFFFFF; z-index: 900100;">
	<div style="position:absolute; width:230px; height:90px; background-image: url('<%= staticResourceUrl(request, "loader-background-element.png")%>'); left:50%; top:50%; margin-left:-115px; margin-top:-45px; text-align: center;">
		<img src='<%= staticResourceUrl(request, "ajax-loader-new.gif")%>' style="margin-top: 20px; margin-bottom: 10px;" />
		<br>
		<span style="font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 11px; text-align: center; color: #222222;">Loading IDE...</span>
	</div>
</div>

<script>
	window["ide-status-changed-listener"] = function(msg) {
		console.log("ide-status-changed-listener [" + msg + "]");
		try {
			if ("load-complete" === msg) {
				var element = document.getElementById("ide-preloader");
				element.parentNode.removeChild(element);
			}		
		} catch (e) {
			console.log("Error: " + e.message);
		}
	}
</script>

<script type="text/javascript" src='<%= staticResourceUrl(request, "_app.nocache.js")%>'></script>

<script type="text/javascript" src='<%= staticResourceUrl(request, "session.js")%>'></script>

<script type="text/javascript" src='<%= staticResourceUrl(request, "menu-additions.js")%>'></script>

<script type="text/javascript" src='<%= staticResourceUrl(request, "statistic.js")%>'></script>

</body>

</html>