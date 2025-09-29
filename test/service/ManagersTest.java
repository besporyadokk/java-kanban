package test.service;

import com.yandex.fz4.service.HistoryManager;
import com.yandex.fz4.service.Managers;
import com.yandex.fz4.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void shouldReturnHistoryManager() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
    }
}