package br.com.tsg.common_rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitConsumerWorker<T> implements Runnable, Closeable {
    private final Logger logger = LoggerFactory.getLogger(RabbitConsumerWorker.class);

    private final RabbitService rabbitService;
    private final String queueName;
    private final Class messageType;
    private final RabbitService.MessageHandler<T> handler;
    private volatile boolean running = true;

    public RabbitConsumerWorker(RabbitService service, String queueName, Class messageType, RabbitService.MessageHandler<T> handler){
        this.rabbitService = service;
        this.queueName = queueName;
        this.messageType = messageType;
        this.handler = handler;
    }

    public void start() throws Exception {
        Thread workerThreaad = new Thread(this, "RabbitConsumerWorker - " + queueName);
        workerThreaad.start();
        logger.info("Rabbit consumer worker started for queue: {}", queueName);
    }

    public void stop(){
        running = false;
        logger.info("RabbitMQ consumer worker stopped for queue: {}", queueName);
    }

    @Override
    public void close() {
        stop();
        try {
            rabbitService.close();
        } catch (IOException | TimeoutException e) {
            logger.error("Error while closing RabbitMQ service: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                rabbitService.registerConsumer(queueName, messageType, handler);
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("Error in RabbitMQ consumer worker: ", e);
            }
        }
    }
}
