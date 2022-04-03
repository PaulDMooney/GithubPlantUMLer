package ca.pauldmooney.githubplantumler

import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import net.sourceforge.plantuml.code.Transcoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/")
class Controller(private val transcoder:Transcoder,
                 private val githubClient: GithubClient,
                @Value("\${plantUML.baseURL}") private val plantUMLBaseURL:String) {

    @GetMapping
    @ApiResponses(*[ApiResponse(responseCode = "302", description = "Redirect to PlantUML")])
    fun redirectToPlantUMLFromRaw(@RequestParam(name="github") githubUrl:String, @RequestParam(name="format", defaultValue = "uml") format:String = "uml"): Mono<ResponseEntity<Void>> {
        return getPlantUMLURLFromRaw(githubUrl, format)
            .map { plantUMLURL -> ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(plantUMLURL)).build() }
    }

    @GetMapping("/plantUMLURL")
    @ApiResponses(*[ApiResponse(responseCode = "200", description = "PlantUML URL Calculated")])
    fun getPlantUMLURLFromRaw(@RequestParam(name="github") githubUrl:String, @RequestParam(name="format", defaultValue = "uml") format:String = "uml"): Mono<String> {
        return githubClient.getContentFromRawPath(githubUrl)
            .map ( transcoder::encode )
            .map { encodedFileContent -> "${plantUMLBaseURL}/${format}/${encodedFileContent}" }
    }
}