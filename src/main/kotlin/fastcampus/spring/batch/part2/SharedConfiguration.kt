package fastcampus.spring.batch.part2

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SharedConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun shareJob(): Job =
        jobBuilderFactory["shareJob"]
            .incrementer(RunIdIncrementer())
            .start(shareStep())
            .next(shareStep2())
            .build()


    private fun shareStep(): Step {
        return stepBuilderFactory["shareStep"]
            .tasklet { contribution, chunkContext ->
                val stepExecution = contribution.stepExecution
                val stepExecutionContext = stepExecution.executionContext
                stepExecutionContext.putString("stepKey", "step execution context")

                val jobExecution = stepExecution.jobExecution
                val jobInstance = jobExecution.jobInstance
                val jobExecutionContext = jobExecution.executionContext
                jobExecutionContext.putString("jobKey", "job execution context")
                val jobParameters = jobExecution.jobParameters

                println(
                    "jobName : ${jobInstance.jobName} , stepName : ${stepExecution.stepName} , parameters : ${
                        jobParameters.getLong(
                            "run.id"
                        )
                    }"
                )

                RepeatStatus.FINISHED
            }
            .build()
    }

    private fun shareStep2(): Step {
        return stepBuilderFactory["shareStep2"]
            .tasklet { contribution, chunkContext ->
                val stepExecution = contribution.stepExecution
                val stepExecutionContext = stepExecution.executionContext

                val jobExecution = stepExecution.jobExecution
                val jobExecutionContext = jobExecution.executionContext


                println(
                    "jobKey : ${
                        jobExecutionContext.getString(
                            "jobKey",
                            "emptyJobKey"
                        )
                    } , stepKey : ${stepExecutionContext.getString("stepKey", "emptyStepKey")}"
                )
                RepeatStatus.FINISHED
            }
            .build()
    }


}