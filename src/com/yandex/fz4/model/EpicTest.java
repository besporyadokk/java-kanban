package com.yandex.fz4.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void EpicWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("a","b");
        Epic epic2 = new Epic("b","a");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1,epic2);
    }

    @Test
    void EpicCouldNotBeAddedToItself (){
        Epic epic = new Epic("a","b");
        epic.setId(64);
        int size = epic.getSubtasksIds().size();
        epic.addSubtaskId(epic.getId());
        assertEquals(size,epic.getSubtasksIds().size());
        //Если число в size не поменелось, значит эпик не добавил сам себя
    }

}