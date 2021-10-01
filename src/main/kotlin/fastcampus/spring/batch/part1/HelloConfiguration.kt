package fastcampus.spring.batch.part1

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelloConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun helloJob(): Job {
        return jobBuilderFactory["helloJob"]
            .incrementer(RunIdIncrementer())
            .start(helloStep())
            .build()
    }

    @Bean
    fun helloStep(): Step {
        return stepBuilderFactory["helloStep"]
            .tasklet { contribution, chunkContext ->
                println("hello Spring batch")
                RepeatStatus.FINISHED
            }
            .build()
    }

}