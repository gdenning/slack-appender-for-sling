Slack Appender For Sling
========================
Logging appender for Sling/AEM that generates Slack webhook events.

Build
-----

To build, simply run "mvn clean install" from the root folder.

Installation
------------

To install this bundle in AEM, open the OSGi web console, click the "OSGi-->Bundles" menu, then click "Install/Update", check the "Start Bundle" checkbox, then choose the "slack-appender-bundle-*.jar" and click "Install or Update".

Configure
---------
To configure this bundle in AEM, open the OSGi web console, click the "OSGi-->Configuration" menu, then search for "SlackLogbackAppender".  Edit the configuration and set the following options:
* stack.trace.type: Set this to "CAUSED_BY" to only submit a limited stack trace with caused by headers.  Set this to "FULL" to submit the complete stack trace.
* loggers: Add a list of loggers to include in the logs that are sent to the Slack webhook.  Usually this matches to class or package names, such as "com.day.cq".
* webhook.url: Set this to the webhook url that was generated inside Slack when you create a "Incoming Webhook" integration.
* min.level: The minimum level of logging to submit to the Slack webhook.
