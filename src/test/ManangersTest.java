package test;

import com.yandex.fz4.service.HistoryManager;
import com.yandex.fz4.service.Manangers;
import com.yandex.fz4.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManangersTest {

    @Test
    void shouldReturnTaskManager() {
        TaskManager manager = Manangers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void shouldReturnHistoryManager() {
        HistoryManager manager = Manangers.getDefaultHistory();
        assertNotNull(manager);
    }
}