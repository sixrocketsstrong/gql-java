![GitHub](https://img.shields.io/github/license/sixrocketsstrong/gql-java)
[![Build Status](https://travis-ci.com/sixrocketsstrong/gql-java.svg?branch=master)](https://travis-ci.com/sixrocketsstrong/gql-java)
[ ![Download](https://api.bintray.com/packages/sixrocketsstrong/maven/gql-java/images/download.svg) ](https://bintray.com/sixrocketsstrong/maven/gql-java/_latestVersion)


# GQL-Java

GQL-Java is a builder-style DSL for assembling GraphQL queries in Java code. It's designed to let you define static elements and assemble them just-in-time. This is much easier to refactor a client call than `static final String` query constants. 

## Getting Started

These instructions will give you a quick overview of how to use GQL-Java to build queries and structure your project for maximum reuse.

### Prerequisites

Java 8+


### Installing

Use the "Download" link above to link to the Bintray repo with instructions on including in your Maven `pom` or Gradle build.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Dave Brown** - *Initial work* - [bangroot](https://github.com/bangroot)

See also the list of [contributors](https://github.com/sixrocketsstrong/gql-java/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* I wanted something like [gql](https://github.com/apollographql/graphql-tag) for JS. While this is not using a templating engine and what not, I was aiming for similar construction and reuse.
