package maquette.core.scheduler;

import maquette.core.scheduler.quartz.QuartzCronScheduler;

public final class CronSchedulers {

    private CronSchedulers() {

    }

    public static CronScheduler apply() {
        return QuartzCronScheduler.apply();
    }

}
