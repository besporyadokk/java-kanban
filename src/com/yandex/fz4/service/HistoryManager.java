package com.yandex.fz4.service;
import com.yandex.fz4.model.*;
import java.util.*;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
