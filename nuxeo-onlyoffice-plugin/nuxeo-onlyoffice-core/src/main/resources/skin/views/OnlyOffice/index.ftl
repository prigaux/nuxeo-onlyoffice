<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${title}</title>
        <link rel="stylesheet" href="${skinPath}/css/editor.css" type="text/css" media="screen" charset="utf-8"/>
        <link rel="icon" href="${skinPath}/favicon.ico" type="image/x-icon" />

        <script type="text/javascript" src="${documentServer}web-apps/apps/api/documents/api.js"></script>

        <script type="text/javascript" language="javascript">

        var docEditor;

        var onDocumentStateChange = function (event) {
            var title = document.title.replace(/\*$/g, "");
            document.title = title + (event.data ? "*" : "");
        };

        var onOutdatedVersion = function (event) {
            location.reload(true);
        };

        var сonnectEditor = function () {

            docEditor = new DocsAPI.DocEditor("iframeEditor",
                {
                    width: "100%",
                    height: "100%",
                    type: "desktop",

                   	${config},

                    events: {
                        "onDocumentStateChange": onDocumentStateChange,
                        "onOutdatedVersion": onOutdatedVersion,
                    }
                });
        };

        if (window.addEventListener) {
            window.addEventListener("load", сonnectEditor);
        } else if (window.attachEvent) {
            window.attachEvent("load", сonnectEditor);
        }

        function getXmlHttp() {
            var xmlhttp;
            try {
                xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (ex) {
                    xmlhttp = false;
                }
            }
            if (!xmlhttp && typeof XMLHttpRequest !== "undefined") {
                xmlhttp = new XMLHttpRequest();
            }
            return xmlhttp;
        }

    </script>

    </head>
    <body>
        <div class="form">
            <div id="iframeEditor"></div>
        </div>
    </body>
</html>