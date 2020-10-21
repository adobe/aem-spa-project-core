# SPA Project Core

[![License](https://img.shields.io/badge/license-Apache%202-blue)](https://github.com/adobe/aem-spa-project-core/blob/master/LICENSE)
[![Version](https://img.shields.io/maven-metadata/v/https/oss.sonatype.org/service/local/repositories/releases/content/com/adobe/aem/spa.project.core.all/maven-metadata.xml.svg?label=Version)](https://mvnrepository.com/artifact/com.adobe.aem/spa.project.core)



**This package contains the components required for building a single-page application using AEM.**

It contains a `Page` interface (extension of the Core Components' `Page` v1 and v2), which adds support for a hierarchical model of subpages with which a single-page application can be built.

The `PageImpl` allows the retrieval of a hierarchical page model in JSON format. The content of the exported model can be configured using parameters. More information can be found in [`PageImpl.java`](./core/src/main/java/com/adobe/aem/spa/project/core/internal/impl/PageImpl.java).

## Installation
The dependency can be found here:
https://mvnrepository.com/artifact/com.adobe.aem/aem.project.core
Simply put the following in your pom.xml:
```
<dependency>
    <groupId>com.adobe.aem</groupId>
    <artifactId>spa.project.core</artifactId>
    <version><Version></version>
    <type>pom</type>
</dependency>
```

## Documentation

* [SPA Editor Overview](https://www.adobe.com/go/aem6_5_docs_spa_en)
* [SPA Architecture](https://docs.adobe.com/content/help/en/experience-manager-65/developing/headless/spas/spa-architecture.html)
* [Getting Started with the AEM SPA Editor and Angular](https://docs.adobe.com/content/help/en/experience-manager-learn/spa-angular-tutorial/overview.html)
* [Getting Started with the AEM SPA Editor and React](https://docs.adobe.com/content/help/en/experience-manager-learn/spa-react-tutorial/overview.html)

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](LICENSE) for more information.
