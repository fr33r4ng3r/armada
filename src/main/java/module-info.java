module armada.main {

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;
    requires kotlin.stdlib.jdk7;
    requires kotlinx.coroutines.core.jvm;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.logging;
    requires java.prefs;
    requires java.net.http;
    requires kotlinx.coroutines.jdk8;
    requires kotlinx.coroutines.javafx;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.web;
    requires reactor.core;
    requires java.desktop;
    requires javafx.swing;
    requires kotlinx.coroutines.reactor;
    requires com.fasterxml.jackson.kotlin;
    requires org.slf4j;

    opens armada to javafx.base, javafx.fxml;
    opens armada.engine.server to spring.core, kotlin.reflect;
    opens armada.engine.api to com.fasterxml.jackson.databind;

    exports armada to javafx.fxml, javafx.graphics;
    exports armada.engine.server to spring.beans, spring.context, spring.webflux, kotlin.reflect;
    exports armada.engine.api to com.fasterxml.jackson.databind;
}