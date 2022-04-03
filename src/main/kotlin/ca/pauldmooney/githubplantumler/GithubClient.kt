package ca.pauldmooney.githubplantumler

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class GithubClient(@Qualifier(Constants.RAW_GITHUB_USERCONTENT_WEBCLIENT) private val webClient: WebClient) {

    /**
     * Path should be everything after the host. Eg, /username/repo/main/path/to/filename.puml
     */
    fun getContentFromRawPath(filePath:String): Mono<String> {
        return webClient.get().uri {uriBuilder -> uriBuilder.path(filePath).build() }
            .retrieve()
            .bodyToMono(String::class.java)
    }
}