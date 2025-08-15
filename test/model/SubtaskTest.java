package test.model;

import com.yandex.fz4.model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtasksCantBeItsEpic() {
        Subtask subtask = new Subtask("a", "b", 1);
        subtask.setId(2);
        int id = subtask.getEpicId();
        subtask.setEpicId(subtask.getId()); // id остаётся прежний и не передаётся в setEpicId
        assertEquals(id, subtask.getEpicId());
    }

    @Test
    void subtaskWithSameIdsShoulBeEqual() {
        Subtask subtask1 = new Subtask("a", "b", 1);
        Subtask subtask2 = new Subtask("b", "a", 2);

        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask2, subtask1);
    }


}