package fastcampus.spring.batch

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableBatchProcessing
@SpringBootApplication
class FastcampusApplication

fun main(args: Array<String>) {
    runApplication<FastcampusApplication>(*args)
}
