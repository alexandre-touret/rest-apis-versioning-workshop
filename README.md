# REST APIs Versionning: Hands-on !

This workshop aims to introduce different ways to handle and propose several versions of a same API to your customers.


## Big picture

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

#### Explanation
Here we have two main kind of users :
* Customer : He can browse and book books
* Administrator: He can create books and activate/desactivate the maintenance mode

Within our platform, we have two main systems:
* Bookstore system which operate all the book related operations
* Bookstore IAM which is responsible for identifying and authorizing users

### Container view

### Customers

## Our API Roadmap

```mermaid
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

## Dealing with updates without versionning

## Our first version

### Creating V1

In the URI, in a header, a mix between the gateway & the apps

### SCM & Configuration management

## Customer's management

## Dealing with conflicts

## Authorization