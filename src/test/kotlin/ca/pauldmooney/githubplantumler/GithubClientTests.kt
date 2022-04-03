package ca.pauldmooney.githubplantumler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.Duration

class GithubClientTests: DescribeSpec() {

    lateinit var githubClient: GithubClient

    lateinit var webClient: WebClient

    lateinit var mockGithubWebServer: MockWebServer

    init {

        beforeContainer {
            mockGithubWebServer = MockWebServer()
            mockGithubWebServer.start()
        }

        beforeEach {
            val gitConfig = GithubConfig(
                userName = "testUser",
                accessToken = "testToken",
                rawUserContentBaseURL = "http://localhost:${mockGithubWebServer.port}",
                allowedFileExtensions = listOf("puml")
            )
            webClient = setupGithubWebClient(gitConfig)
            githubClient = GithubClient(webClient, gitConfig)
        }

        afterContainer {
            mockGithubWebServer.shutdown()
        }

        describe("getContentFromRawPath") {
            describe("with valid path") {

                it("should return contents of file") {

                    // Given
                    val fileContent = "This is the file content"
                    val fileUrl = "/path/to/file.puml"
                    mockGithubWebServer.enqueue(MockResponse().setResponseCode(200).setBody(fileContent))

                    // When
                    val response = githubClient.getContentFromRawPath(fileUrl)

                    // Then
                    StepVerifier.create(response)
                        .expectNext(fileContent)
                        .then {
                            val recordedRequest = mockGithubWebServer.takeRequest()
                            recordedRequest.path.shouldBe(fileUrl)
                        }
                        .verifyComplete()
                }
            }

            describe("with not-allowed file extension") {
                it("should throw an error") {

                    // Given
                    val fileUrl = "path/to/file.bad"

                    // When
                    var response = githubClient.getContentFromRawPath(fileUrl)

                    // Then
                    StepVerifier.create(response).expectError().verify(Duration.ofSeconds(5));
                }
            }

            describe("with allowed file extension, in a different case") {
                it("should return contents of file") {

                    // Given
                    val fileContent = "This is the file content"
                    val fileUrl = "/path/to/file.PUML"
                    mockGithubWebServer.enqueue(MockResponse().setResponseCode(200).setBody(fileContent))

                    // When
                    val response = githubClient.getContentFromRawPath(fileUrl)

                    // Then
                    StepVerifier.create(response)
                        .expectNext(fileContent)
                        .then {
                            val recordedRequest = mockGithubWebServer.takeRequest()
                            recordedRequest.path.shouldBe(fileUrl)
                        }
                        .verifyComplete()
                }
            }
        }
    }
}