package maquette.core.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import org.quartz.Scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Value
@AllArgsConstructor(staticName = "apply")
public class JobDetails {

    String name;

    String cronExpression;

    LocalDateTime nextExecution;

    public static JobDetails apply(ScheduledJob job,
                                   Scheduler scheduler) {
        var nextExecution = Operators
            .suppressExceptions(() -> scheduler.getTrigger(job
                .getTrigger()
                .getKey()))
            .getNextFireTime()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        return apply(job.getName(), job.getCronExpression(), nextExecution);
    }

}
