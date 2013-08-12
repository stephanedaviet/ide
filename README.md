# eXo IDE 3.0

How to run IDE 3 demo application:
1. Build all IDE3 modules from the root of the project:
mvn clean install
2. Go codenvy-packaging-standalone-tomcat/target/tomcat-ide and start Tomcat
3. Go to codenvy-ide-client
mvn clean gwt:run -Dgwt.noserver=true
4. Wait until button "Launch Default Browser" appears in the top panel of the window.
Open in browser http://127.0.0.1:8080/IDE/IDE.html?gwt.codesvr=127.0.0.1:9997

How to run Codenvy with supporting of running Codenvy-extensions:
1. Build all modules from the root of the project using profile 'ext-runtime':
mvn clean install -Pext-runtime
2. Go to codenvy-packaging-standalone-tomcat/target/tomcat-ide and start Tomcat.
