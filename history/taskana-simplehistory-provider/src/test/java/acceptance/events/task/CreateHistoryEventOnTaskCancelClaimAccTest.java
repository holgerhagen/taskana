package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCancelClaimAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateCancelClaimedHistoryEvent_When_TaskIsCancelClaimed() throws Exception {

    final String taskId = "TKI:000000000000000000000000000000000043";

    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

    assertThat(events).isEmpty();

    assertThat(taskService.getTask(taskId).getState()).isEqualTo(TaskState.CLAIMED);
    Task task = taskService.forceCancelClaim(taskId);
    assertThat(task.getState()).isEqualTo(TaskState.READY);

    events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType).isEqualTo(TaskHistoryEventType.CLAIM_CANCELLED.getName());

  }
}
