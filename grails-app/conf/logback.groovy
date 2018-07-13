import ch.qos.logback.core.status.OnConsoleStatusListener
import grails.util.BuildSettings
import grails.util.Environment


statusListener(OnConsoleStatusListener)

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}


def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

logger 'com.joelforjava.ripplr', DEBUG, ['STDOUT'], false

root(ERROR, ['STDOUT'])

