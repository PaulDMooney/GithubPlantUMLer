package ca.pauldmooney.githubplantumler

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import net.sourceforge.plantuml.code.Transcoder
import net.sourceforge.plantuml.code.TranscoderUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableWebFlux
@EnableConfigurationProperties(GithubConfig::class)
@OpenAPIDefinition(info = Info(title="APIs"))
class GithubPlantUMLerApplication {
	@Bean
	fun transcoder():Transcoder = TranscoderUtil.getDefaultTranscoder()

	@Bean @Qualifier(Constants.RAW_GITHUB_USERCONTENT_WEBCLIENT)
	fun githubWebClient(githubConfig: GithubConfig):WebClient = setupGithubWebClient(githubConfig)
}

fun main(args: Array<String>) {
	runApplication<GithubPlantUMLerApplication>(*args)
}

object Constants {
	const val RAW_GITHUB_USERCONTENT_WEBCLIENT = "RAW_GITHUB_USERCONTENT_WEBCLIENT"
}