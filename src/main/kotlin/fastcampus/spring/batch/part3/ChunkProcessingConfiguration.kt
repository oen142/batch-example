package fastcampus.spring.batch.part3

import io.micrometer.core.instrument.util.StringUtils
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChunkProcessingConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,

    ) {

    @Bean
    fun chunkProcessingJob(): Job {
        return jobBuilderFactory["chunkProcessingJob"]
            .incrementer(RunIdIncrementer())
            .start(taskBaseStep())
            .next(chunkBaseStep(null))
            .build()
    }

    @Bean
    fun taskBaseStep(): Step {
        return stepBuilderFactory["taskBaseStep"]
            .tasklet(this.tasklet())
            .build()
    }

    @Bean
    @JobScope
    fun chunkBaseStep(
        @Suppress("SpringElInspection") @Value("#{jobParameters[chunkSize]}")
        chunkSize: String?
    ): Step {
        return stepBuilderFactory["chunkBaseStep"]
            .chunk<String, String>(Integer.parseInt(chunkSize))
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(itemWriter())
            .build()
    }

    private fun itemReader(): ItemReader<String> {
        return ListItemReader(getItems())
    }

    private fun itemProcessor(): ItemProcessor<String, String> {
        return ItemProcessor { item ->
            print(item)
            " $item , Spring Batch"
        }
    }

    private fun itemWriter(): ItemWriter<String> {
        return ItemWriter { items ->
            println("chunk item size : ${items.size}")
            items.forEach {
                print(it)
            }
        }
    }


    private fun tasklet(): Tasklet {
        val items = getItems()

        return Tasklet { contribution, chunkContext ->
            val stepExecution = contribution.stepExecution
            val jobParameters = stepExecution.jobParameters

            val value = jobParameters.getString("chunkSize", "10")
            val chunkSize = Integer.parseInt(value)
            val fromIndex = stepExecution.readCount
            val toIndex = fromIndex + chunkSize

            if (fromIndex >= items.size) {
                return@Tasklet RepeatStatus.FINISHED
            }
            val subList = items.subList(fromIndex, toIndex)


            items.forEach {
                println("${subList.size} hello")
            }
            stepExecution.readCount = toIndex

            return@Tasklet RepeatStatus.CONTINUABLE
        }
    }

    private fun getItems(): List<String> {
        val list = mutableListOf<String>()
        for (i in 1..100) {
            list.add("$i hello")

        }
        return list
    }
}