package test.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.fz4.http.HttpTaskServer;
import com.yandex.fz4.service.Managers;
import com.yandex.fz4.service.TaskManager;
import com.yandex.fz4.util.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.time.LocalDateTime;

public class BaseHttpTest {
    protected TaskManager taskManager;
    protected HttpTaskServer taskServer;
    protected Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}