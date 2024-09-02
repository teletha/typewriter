/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.surreal;

import java.util.concurrent.TimeUnit;

import com.surrealdb.connection.SurrealWebSocketConnection;

import kiss.I;
import kiss.Signal;
import kiss.WiseRunnable;
import psychopath.File;

public class SurrealManager {

    /** The server address. */
    private String url = "127.0.0.1";

    /** The server port. */
    private int port = 8000;

    /** The running mode. */
    private String mode = "memory";

    /** The server mode. */
    private boolean automaticShutdown = true;

    /** The log level. */
    private String logLevel = "info";

    /** The executable file. */
    private File executable;

    /** The server state. */
    private boolean launching;

    /**
     * Create database manager.
     * 
     * @return
     */
    public static SurrealManager with() {
        return new SurrealManager();
    }

    /**
     * Hide constructor.
     */
    private SurrealManager() {
    }

    /**
     * Configure server address. (default: 127.0.0.1:8000)
     * 
     * @param url
     * @param port number
     * @return
     */
    public SurrealManager url(String url, int port) {
        this.url = url;
        this.port = port;
        return this;
    }

    /**
     * Configure server mode. (default: memory)
     * 
     * @return
     */
    public SurrealManager mode(String mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Configure server mode. (default: true)
     * 
     * @param enable
     * @return
     */
    public SurrealManager autoClose(boolean enable) {
        this.automaticShutdown = enable;
        return this;
    }

    /**
     * Configure logging level. (default: info)
     * 
     * @param level
     * @return
     */
    public SurrealManager logLevel(String level) {
        this.logLevel = level;
        return this;
    }

    /**
     * Configure the location of SurrealDB.
     * 
     * @param file
     * @return
     */
    public SurrealManager executable(File file) {
        this.executable = file;
        return this;
    }

    /**
     * Establish the connection to the database.
     * 
     * @return
     */
    public synchronized Signal<SurrealWebSocketConnection> establish() {
        if (launching == false) {
            launching = true;

            Thread thread = new Thread((WiseRunnable) () -> {
                ProcessBuilder builder = new ProcessBuilder(executable.path(), "start", "--log", logLevel, "--bind", url + ":" + port, mode)
                        .directory(executable.parent().asJavaFile())
                        .inheritIO();

                Process process = builder.start();

                if (automaticShutdown) {
                    Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
                }
            });
            thread.setName("Surreal Process");
            thread.setDaemon(true);
            thread.start();
        }

        return I.signal(0).map(x -> {
            SurrealWebSocketConnection connection = new SurrealWebSocketConnection(url, port, false);
            connection.connect(1);
            return connection;
        }).retry(x -> x.delay(1, TimeUnit.SECONDS).take(10));
    }
}
