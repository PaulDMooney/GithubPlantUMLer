package ca.pauldmooney.githubplantumler

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.web.reactive.function.client.WebClient

@ConstructorBinding
@ConfigurationProperties(prefix = "github")
data class GithubConfig(
    val userName: String?,
    val accessToken: String?,
    val rawUserContentBaseURL: String,
    val allowedFileExtensions:List<String> = listOf() ) {
}

fun setupGithubWebClient(githubConfig: GithubConfig): WebClient =
    WebClient.builder().baseUrl(githubConfig.rawUserContentBaseURL)
        .defaultHeaders { headers ->
            if (githubConfig.userName != null && githubConfig.accessToken != null) {
                headers.setBasicAuth(githubConfig.userName, githubConfig.accessToken)
            }
        }
        .build()