package ca.pauldmooney.githubplantumler

import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class GithubClient(
    @Qualifier(Constants.RAW_GITHUB_USERCONTENT_WEBCLIENT) private val webClient: WebClient,
    private val githubConfig: GithubConfig) {

    /**
     * Path should be everything after the host. Eg, /username/repo/main/path/to/filename.puml
     */
    fun getContentFromRawPath(filePath:String): Mono<String> {

        var goodExtension = true;
        if (githubConfig.allowedFileExtensions.isNotEmpty()) {
            val extension:String = FilenameUtils.getExtension(filePath)
            goodExtension = githubConfig.allowedFileExtensions.stream().anyMatch { extension.equals (it, ignoreCase = true) }
        }

        if (!goodExtension) {
            return Mono.error(ExtensionNotAllowed())
        }

        return webClient.get().uri {uriBuilder -> uriBuilder.path(filePath).build() }
            .retrieve()
            .bodyToMono(String::class.java)
    }
}

class ExtensionNotAllowed:java.lang.Exception("File extension not allowed")