package com.yandex.fz4.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManangersTest {

    @Test
    void shouldReturnTaskManager(){
        TaskManager manager = Manangers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void shouldReturnHistoryManager(){
        HistoryManager manager = Manangers.getDefaultHistory();
        assertNotNull(manager);
    }
}