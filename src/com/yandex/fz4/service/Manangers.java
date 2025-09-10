package com.yandex.fz4.service;

import java.io.*;

public class Manangers {


    public static TaskManager getDefault() {
        try {
            File tempFile = File.createTempFile("tasks", ".txt");
            return FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось создать временный файл" + e.getMessage());
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
