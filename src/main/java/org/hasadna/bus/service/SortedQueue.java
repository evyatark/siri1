package org.hasadna.bus.service;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class SortedQueue {

    void put(Command command) {
        queue.offer(command);
    }

    Command takeFromQueue() {
        return queue.poll();
    }

    // beware! this operation takes O(n) and is not accurate
    int size() {
        return queue.size();
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

    private Queue<Command> queue = new ConcurrentLinkedQueue<>();

}
