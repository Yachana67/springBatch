package com.batch.springBatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfiguration {
	@Autowired
	private JobLauncher jobLauncher;

    @Autowired
    private Job readExcelJob;

 @Scheduled(fixedDelay = 20000) // Run every 20 seconds
    public void runJob() throws JobExecutionException {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(readExcelJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
