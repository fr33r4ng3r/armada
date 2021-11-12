package armada.engine.server

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@SpringBootApplication
open class Server {

    @Bean
    @Scope("prototype")
    open fun jacksonObjectMapperBuilder(applicationContext: ApplicationContext, customizers: List<Jackson2ObjectMapperBuilderCustomizer>): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.applicationContext(applicationContext)
        customizers.forEach {customizer->
            customizer.customize(builder)
        }

        builder.modulesToInstall(KotlinModule::class.java)
        return builder
    }
}