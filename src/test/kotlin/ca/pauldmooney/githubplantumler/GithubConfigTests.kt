package ca.pauldmooney.githubplantumler

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

class GithubConfigTests: DescribeSpec() {

    lateinit var mockWebServer: MockWebServer

    init {

        beforeContainer {
            mockWebServer = MockWebServer()
            mockWebServer.start()
        }

        afterContainer {
            mockWebServer.shutdown()
        }

        describe("setupGithubWebClient") {

            beforeEach {
                mockWebServer.enqueue(MockResponse().setResponseCode(200))
            }

            it("should setup authorization header in WebClient when non null userName and accessToken") {

                // Given
                val githubConfig = GithubConfig(userName = "testUser", accessToken = "testAccessToken", "http://localhost:${mockWebServer.port}")

                // When
                val webClient = setupGithubWebClient(githubConfig)
                val result = webClient.get().retrieve().toMono()

                // Then
                StepVerifier.create(result)
                    .expectNext()
                    .then {
                        val recordedRequest = mockWebServer.takeRequest()
                        recordedRequest.headers["Authorization"].shouldNotBeNull().shouldNotBeBlank()
                    }

            }

            describe("authorization header should be empty") {
                forAll(null to null,
                    null to "notempty",
                    null to "",
                    "" to null,
                    "" to "notempty",
                    "" to "",
                    "notempty" to "",
                    "notempty" to null) { (userName, accessToken) ->

                    // Given
                    val githubConfig = GithubConfig(userName, accessToken, rawUserContentBaseURL = "http://localhost:${mockWebServer.port}")

                    // When
                    val webClient = setupGithubWebClient(githubConfig)
                    val result = webClient.get().retrieve().toMono()

                    // Then
                    StepVerifier.create(result)
                        .expectNext()
                        .then {
                            val recordedRequest = mockWebServer.takeRequest()
                            recordedRequest.headers["Authorization"].shouldBeNull()
                        }

                }
            }
        }
    }
}