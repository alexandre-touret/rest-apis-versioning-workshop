# REST APIs Versionning: Hands-on !

This workshop aims to introduce different ways to handle and propose several versions of a same API to your customers.

## :dart: Big picture

During this workshop we will strive with API versionning on a (small) microservice application.
Here is a short description of it.

This platform aims to store and get books of a bookstore.

### System View 

```mermaid
C4Context
      title System Context diagram for Bookstore System
      Person(customerA, "Bookstore Customer", "A customer of the bookstore") 
      Person(adminA, "Bookstore Administrator", "An administrator of the bookstore") 
      Enterprise_Boundary(b0, "Bookstore Boundary") {
        System(bookstoreSystem, "Bookstore System", "Allows Book creation, search,...")  
        System(iamSystem, "Bookstore IAM", "Allows Identification & authorization...")  
      }
      Rel(customerA, bookstoreSystem, "Uses")
      Rel(adminA, bookstoreSystem, "Uses & manage users")
      Rel(customerA, iamSystem, "identifies & authorizes")
      Rel(adminA, iamSystem, "identifies & authorizes")
```

#### Explanations
Here we have two main kind of users :
* Customer : He can browse and book books
* Administrator: He can create books and activate/desactivate the maintenance mode

Within our platform, we have two main systems:
* Bookstore system which operate all the book related operations
* Bookstore IAM which is responsible for identifying and authorizing users

### Container view


```mermaid
C4Container
      title Container Context diagram for Bookstore System


      Person(customerA, "Bookstore Customer", "A customer of the bookstore") 
      Person(adminA, "Bookstore Administrator", "An administrator of the bookstore") 

      Enterprise_Boundary(b0, "Bookstore Boundary") {
        Container_Boundary(b2,"Bookstore IAM"){
          Container(iam,"IAM Mock","Provides a JWT token with roles in claims")

               }
        Container_Boundary(b1,"Bookstore System"){
          
            Container(gateway,"API Gateway","Spring Cloud Gateway","Exposes the APIs")
            Container(bookstoreApi,"Bookstore API","Spring Boot, Cloud","Exposes the Bookstore APIs")
            Container(configuration,"Configuration Server","Spring Cloud Config","Exposes the configuration")
            Container(isbnApi,"ISBN","Spring Boot, Cloud","Exposes the ISBN APIs")
            ContainerDb(database, "Database", "PostgreSQL Database", "Stores bookstore")
        }
      }

      Rel(customerA,gateway, "Uses")
      Rel(adminA, gateway, "Uses & manage users")
      Rel(customerA, iam, "identifies & authorizes")
      Rel(adminA, iam, "identifies & authorizes")

      Rel(gateway, bookstoreApi, "exposes")
      Rel(bookstoreApi, isbnApi, "uses")
      Rel(bookstoreApi, database, "stores data")
      UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="2")
```

#### Explanations

This diagram dig into the systems exposed above in the system view.

The Bookstore system is composed of:
* The API Gateway which exposes our APIs
* Bookstore API which exposes all the related book APIs and stores data to a PostgreSQL database
* ISBN API which provides random ISBN numbers
* Configuration server which centralises all the configuration files

The Bookstore IAM is composed of:
* a mock server which provides JWT token with appropriate roles and informations

### :straight_ruler: Stack
Here is a summary of the stack used in this workshop for this architecture:

| Container | Tools | Comments |
|---|---|---|
| API Gateway | Spring Cloud Gateway 2021.0.4  |  |
| Bookstore API | JAVA 17,Spring Boot 2.7.X |  |
| ISBN API | JAVA 17,Spring Boot 2.7.X |  |
| Configuration Server | Spring Cloud Config 2021.0.4 |  |
| Database | PostgreSQL |  |
| IAM Mock |  |  |


### Customers

## Our API Roadmap

```mermaid
%%{init: { 'logLevel': 'debug', 'theme': 'base', 'gitGraph': {'rotateCommitLabel': true}} }%%
gitGraph:
    commit id:"Init"
    commit id: "new features" tag:"Adding new attributes & operations"
    branch V1
    checkout V1
    commit id:"first update"
    commit tag:"Revamping Bookstore API for customer 1"
    checkout main
    branch V2
    commit id: "revamping" tag:"revamping for customer 2"
    merge V1
    commit tag:"Deprecating V1"
```

## :traffic_light: Prerequisites

### :mortar_board: Skills

| Skill | Level | 
|---|---|
| REST API | proficient |
| Java | novice |
| Gradle | novice |
| Spring Framework, Gateway | novice |
| OpenID Connect | novice |
| Docker | novice |

### :wrench: Tools 
#### If you want to execute this workshop locally
You must have set up these tools first:
* Java 17+
* Gradle 7.5+
* Docker & Docker compose
* Any IDE (IntelliJ IDEA, VSCode, Emacs,...)
* [cURL](https://curl.se/), [HTTPie](https://httpie.io/) or any tool to call your REST APIs

#### :rocket: If you don't want to bother with a local setup

You can use [Gitpod](https://gitpod.io). 
You must create an account first. 

You then can open this project in either your local VS Code or directly in your browser

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#github.com/alexandre-touret/rest-apis-versionning-workshop.git)

## :boom: Ready ?

:warning: I strongly suggest to fork this project into your personal github namespace. You then can change the URL mentioned above to link github and gitpod:

```markdown
[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#github.com/%%MY_NAMESPACE%%/rest-apis-versionning-workshop.git)
```

or you can directly browse this URL (think to change the ``%%MY_NAMESPACE%%`` prefix):

``https://gitpod.io/#github.com/%%MY_NAMESPACE%%/rest-apis-versionning-workshop.git``

Now, you can start [the workshop](./docs/index.md).

