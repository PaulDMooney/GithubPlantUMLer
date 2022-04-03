# Github PlantUML-er
This is an API that takes a github URL to a PlantUML file (puml) as an argument
and will redirect to a visual rendering of that PlantUML diagram. 
This is useful for being able to create links or html `<img>`s to a rendering of the PlantUML file 
without the need to pre-render it and host the output anywhere.

For example in C4, you can create now create a clickable link from a `System` in a context diagram to the container
diagram that shows more detail about that `System` by adding a link beside the `System` declaration:
```plantuml
System(banking_system, "Internet Banking System", "Allows customers to view information about their bank accounts, and make payments.")[[http://githubplantumlerhost/?github=someuser/somerepo/somebranch/path/to/containerdiagram.puml&format=svg]]
```

## Prerequisites
* Java 17
* Docker

## Build and Run
This is a typical Spring boot application. It can be run locally via `./gradlew bootRun`.
Or by building and running with docker-compose `./gradlew build && docker-compose up --build`

## Configuration
Github PlantUML-er runs without configuration out of the box, however most will be interested in accessing puml files 
from private repos. For this, Github PlantUML-er needs to be configured with a username and accessToken that can access
those private repos:
* `github.userName` = Optional. This is a spring boot property so there may be different permutations that work as well
  such as `github_userName`.
* `github.accessToken` = Optional. This is a spring boot property so there may be different permutations that work as well
  such as `github_accessToken`. It may be possible to use the given user's actual password here but it is not suggested.
* `github.allowedFileExtensions` = A list of the allowed file extensions this application will accept. This is a security feature
used to help prevent visibility into any other source code in a private repo through this tool. Altering this requires
  some knowledge of how Spring boot deals with [list configurations](https://github.com/spring-projects/spring-boot/wiki/Relaxed-Binding-2.0#lists-1)

Other configurations to override can be found in `application.yml`.  

## Using the API
Once the API is up and running you can use it like so:

`{host}/?github={githubPath}&format={format}` and expect a 302 to a rendering of the file at `{githubpath}` on plantuml.com.

Where
* `{format}` is the format you wish to redirect to. Options are `uml`, `svg`, `png`. This is optional and defaults to `uml`.
* `{githubpath}` is the raw path to your plantuml file on github. This can be found by viewing the "raw" content of that file on github.com
and then copying the path in the address bar after the host. 
  For example if your file is `https://github.com/plantuml-stdlib/C4-PlantUML/blob/master/samples/C4_Container%20Diagram%20Sample%20-%20bigbankplc.puml` 
  then clicking the "raw" button gets this: `https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/samples/C4_Container%20Diagram%20Sample%20-%20bigbankplc.puml`
  then cut out the host and `/plantuml-stdlib/C4-PlantUML/master/samples/C4_Container%20Diagram%20Sample%20-%20bigbankplc.puml` is your `{githubpath}`.

### Example
`http://localhost:8080/?github=/plantuml-stdlib/C4-PlantUML/master/samples/C4_Container%20Diagram%20Sample%20-%20bigbankplc.puml&format=svg`
