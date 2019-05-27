package ru.javaops.masterjava.akka;

import akka.actor.ActorSystem;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;

@Slf4j
public class AkkaActivator {
    private static final String AKKA_CONF = "akka.conf";

    private ActorSystem system;

    private AkkaActivator(String actorSystemName, String nodeName) {
        log.info("Start AKKA System {} : {}", actorSystemName, nodeName);
        system = ActorSystem.create(actorSystemName, Configs.getAppConfig(AKKA_CONF).getConfig(nodeName));
    }

    public static AkkaActivator start(String actorSystemName, String configFile) {
        return new AkkaActivator(actorSystemName, configFile);
    }

    public void shutdown() {
        if (system != null) {
            log.info("AKKA System shutdown");
            system.terminate();
        }
    }
}
