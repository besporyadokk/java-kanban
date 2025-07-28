package com.yandex.fz4.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void TasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("a","b");
        Task task2 = new Task("b","a");

        task1.setId(1);
        task2.setId(1);
        assertEquals(task2,task1);
    }



}