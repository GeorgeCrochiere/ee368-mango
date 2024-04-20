<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<tag:page>
    <jsp:attribute name="styles">
        <style>
            html>body .dojoTreeNodeLabelSelected {
                background-color: inherit;
                color: inherit;
            }

            .watchListAttr {
                min-width: 600px;
            }

            .rowIcons img {
                padding-right: 3px;
            }

            html>body .dojoSplitContainerSizerH {
                border: 1px solid #FFFFFF;
                background-color: #F07800;
                margin-top: 4px;
                margin-bottom: 4px;
            }

            .wlComponentMin {
                top: 0px;
                left: 0px;
                position: relative;
                margin: 0px;
                padding: 0px;
                width: 16px;
                height: 16px;
            }
        </style>
    </jsp:attribute>

    <jsp:body>
        <h1 class="smallTitle">Welcome</h1>
        <div dojoType="SplitContainer" orientation="horizontal" sizerWidth="3" activeSizing="true" class="borderDiv" widgetId="splitContainer" style="width: 100%; height: 500px;">
            <div dojoType="ContentPane" sizeMin="20" sizeShare="20" style="overflow:auto;padding:2px;">
                <h2 class="smallTitle">About Mango</h2>
                <p>Mango is an application designed to enhance your experience with smart home sensors. Our technology allows you to monitor your environment with precision and convenience. The software can provide detailed reports and visual diagnostic data.</p>
                <h2 class="smallTitle">Sensors Utilized</h2>
                <p>Mango harnesses the power of sensors to provide you with real-time data about your surroundings. From temperature and humidity to air quality and beyond, Mango keeps you informed and in control.</p>
                <p><a href="/watch_list.shtm">Link to watchlist</a></p>
            </div>
            <div dojoType="ContentPane" sizeMin="50" sizeShare="50" style="overflow:auto; padding:2px 10px 2px 2px;">
                <div class="smallTitle"><fmt:message key="common.help"/></div>
                <br/>

                <c:if test="${sessionUser.firstLogin}"><h2><fmt:message key="dox.welcomeToMango"/>!</h2></c:if>
                <c:set var="filepath">/WEB-INF/dox/<fmt:message key="dox.dir"/>/help.html</c:set>
                <jsp:include page="${filepath}"/>
            </div>
            <div class="dojoSplitContainerVirtualSizerH" style="position: absolute; display: none; z-index: 10;"></div>
        </div>
    </jsp:body>
</tag:page>