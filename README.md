# SPA Project Core

**This package contains the components required for building a single-page application using AEM.**

It contains a `Page` interface (extension of the Core Components' `Page` v1 and v2), which adds support for a hierarchical model of subpages with which a single-page application can be built.

The `PageImpl` allows the retrieval of a hierarchical page model in JSON format. The content of the exported model can be configured using parameters. More information can be found in [`PageImpl.java`](./core/src/main/java/com/adobe/aem/spa/project/core/internal/impl/PageImpl.java).

## Release

For releasing the project to public repo please follow steps on [wiki](https://wiki.corp.adobe.com/pages/viewpage.action?pageId=2162922347)

## Contributing

Contributions are welcomed! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](LICENSE) for more information.
