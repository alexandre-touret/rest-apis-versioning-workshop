# REST APIs Versionning: Hands-on !

This workshop aims to introduce different ways to handle and propose several versions of a same API to your customers.


## Big picture of the application

```mermaid
C4Context
      title System Context diagram for Bookstore System
      Enterprise_Boundary(b0, "Bookstore Boundary") {
        Person(customerA, "Bookstore Customer", "A customer of the bookstore")
        Person(customerB, "Banking Customer B")      
        
        
        System(SystemAA, "Bookstore System", "Allows Book creation, search,...")  
      }
      
      BiRel(customerA, SystemAA, "Uses")
      BiRel(SystemAA, SystemE, "Uses")
      UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

## Our API Roadmap

## Dealing with updates without versionning

## Our first version

In the URI, in a header, a mix between the gateway & the apps

### SCM & Configuration management

## Customer's management

## Dealing with conflicts

## Authorization