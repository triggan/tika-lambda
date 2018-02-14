# Tika-Lambda

Apache Tika is a commonly used open source library for extracting raw text from a number of known document/file types.  The following project (originally started by ) allows one to take advantage of Tika's capabilities running on top of AWS Lambda.  In this manner, Tika can be deployed within many different event-driven architectures on AWS to easily extract text from unstructured data and push this text to other services (i.e. S3, Comprehend, Redshift, etc.).

## Getting Started

The following instructions detail how you can use the framework of this project to build Tika capability into your Serverless architectures.

### Prerequisites

To get started, you'll need the following installed on your development machine:

- Apache Maven (3.5.2 or later)
- Oracle Java JDK (1.8 or later)
- AWS CLI (1.14.24 or later) - found here: https://github.com/aws/aws-cli
- AWS Serverless Application Model Local (0.2.2 or later) - found here: https://github.com/awslabs/aws-sam-local

(The latter two have their own dependencies.  However, if you find yourself doing a lot of Serverless development with Lambda, I highly recommend going through the time to setup each of these tools.)

### Installing

A step by step series of examples that tell you have to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
