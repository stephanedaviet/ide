<%--

    Copyright (c) 2012-2014 Codenvy, S.A.
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    which accompanies this distribution, and is available at
    http://www.eclipse.org/legal/epl-v10.html

    Contributors:
      Codenvy, S.A. - initial API and implementation

--%>
<%@ page import="com.codenvy.commons.env.EnvironmentContext" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Codenvy Developer Environment</title>
    <link rel="shortcut icon" href="/ide/_app/favicon.ico"/>

    <script type="text/javascript" language="javascript">

        /**
         * Base IDE object
         */

        window.IDE = {};

        /**
         * Initial configuration
         */

        window.IDE.config = {

            "workspaceName": <%= EnvironmentContext.getCurrent().getWorkspaceName() == null ? null : "\"" + EnvironmentContext.getCurrent().getWorkspaceName() + "\"" %>,

            "workspaceId": <%= EnvironmentContext.getCurrent().getWorkspaceId() == null ? null : "\"" + EnvironmentContext.getCurrent().getWorkspaceId() + "\"" %>,

            "projectName": window.location.pathname.split("/")[3] ? window.location.pathname.split("/")[3] : null,

            "startupParams": location.search ? location.search.substring(1) : null,

            "hiddenFiles": ".*",

            "facebookLikeURL": "/ide/_app/facebook-like.html",

            "googleLikeURL": "/ide/_app/google-like.html"

        };

        /**
         * Event handlers
         */

        window.IDE.eventHandlers = {};

        /*
        window.IDE.eventHandlers.switchToDashboard = function() {
            window.location.href="/dashboard";
        };
        */

        /*
        window.IDE.eventHandlers.initializationFailed = function(message) {
            if (message) {
                window.alert(message);
            } else {
                window.alert("Unable to initialize IDE");
            }
        };
        */

    </script>

    <script type="text/javascript" src="/ide/_app/greetings/greetings.js"></script>
    <script type="text/javascript" language="javascript" src="/ide/_app/browserNotSupported.js"></script>
    <script type="text/javascript" language="javascript" src="/ide/_app/_app.nocache.js"></script>
</head>
</html>