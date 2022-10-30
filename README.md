# REST APIs Versionning: Hands-on !

This workshop aims to introduce different ways to handle and propose several versions of a same API to your customers.


## Big picture of the application

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

## Our API Roadmap

## Dealing with updates without versionning

## Our first version

In the URI, in a header, a mix between the gateway & the apps

### SCM & Configuration management

## Customer's management

## Dealing with conflicts

## Authorization