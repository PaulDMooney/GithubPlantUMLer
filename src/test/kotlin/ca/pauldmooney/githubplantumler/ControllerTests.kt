package ca.pauldmooney.githubplantumler

import io.kotest.assertions.eq.eq
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class ControllerTests(@LocalServerPort port:Int,
                      @MockBean var githubClient: GithubClient,
                      @Value("\${plantUML.baseURL}") private val plantUMLBaseURL:String): DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {

        lateinit var webTestClient: WebTestClient;

        val fileContent = "Test file content"
        val encodedContent = "2qajBb58oyn9LKZEpoj9pIi10000"

        beforeContainer {
            webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port/").build();
        }

        describe("GET /plantUMLURL?github=/path/to/file.puml") {
            describe("when 'github' path is valid") {
                it("should return the plantUML URL") {

                    // Given
                    val filePath = "/path/to/file.puml"
                    whenever(githubClient.getContentFromRawPath(ArgumentMatchers.anyString()))
                        .thenReturn(Mono.just(fileContent))

                    // When
                    val response = webTestClient.get().uri("/plantUMLURL?github=${filePath}").exchange()

                    // Then
                    response.expectStatus().is2xxSuccessful().expectBody(String::class.java).isEqualTo("${plantUMLBaseURL}/uml/${encodedContent}")

                }
            }
        }

        describe("GET /?github=/path/to/file.puml") {
            describe("when 'github' path is valid") {
                it("should return the a redirect to the plantUML URL") {

                    // Given
                    val filePath = "/path/to/file.puml"
                    whenever(githubClient.getContentFromRawPath(ArgumentMatchers.anyString()))
                        .thenReturn(Mono.just(fileContent))

                    // When
                    val response = webTestClient.get().uri("/?github=${filePath}").exchange()

                    // Then
                    response.expectStatus().isEqualTo(HttpStatus.FOUND)
                        .expectHeader().location("${plantUMLBaseURL}/uml/${encodedContent}")
                }
            }
        }
    }
}
